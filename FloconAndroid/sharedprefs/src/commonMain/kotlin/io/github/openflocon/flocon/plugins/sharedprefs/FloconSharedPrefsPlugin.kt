package io.github.openflocon.flocon.plugins.sharedprefs

import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.FloconConfig
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.FloconPlugin
import io.github.openflocon.flocon.FloconPluginConfig
import io.github.openflocon.flocon.FloconPluginFactory
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.core.decode
import io.github.openflocon.flocon.core.encode
import io.github.openflocon.flocon.error.pluginNotInitialized
import io.github.openflocon.flocon.plugins.sharedprefs.model.FloconPreference
import io.github.openflocon.flocon.plugins.sharedprefs.model.FloconPreferenceValue
import io.github.openflocon.flocon.plugins.sharedprefs.model.PreferencesDescriptor
import io.github.openflocon.flocon.plugins.sharedprefs.model.fromdevice.PreferenceRowDataModel
import io.github.openflocon.flocon.plugins.sharedprefs.model.fromdevice.SharedPreferenceValueResultDataModel
import io.github.openflocon.flocon.plugins.sharedprefs.model.todevice.ToDeviceEditSharedPreferenceValueMessage
import io.github.openflocon.flocon.plugins.sharedprefs.model.todevice.ToDeviceGetSharedPreferenceValueMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FloconPreferencesConfig : FloconPluginConfig

interface FloconPreferencesPlugin : FloconPlugin {
    fun register(preference: FloconPreference)
}

object FloconPreferences : FloconPluginFactory<FloconPreferencesConfig, FloconPreferencesPlugin> {
    override val name: String = "Preferences"
    override val pluginId: String = Protocol.ToDevice.SharedPreferences.Plugin
    override fun createConfig(context: FloconContext) = FloconPreferencesConfig()
    override fun install(
        pluginConfig: FloconPreferencesConfig,
        floconConfig: FloconConfig,
        encoder: FloconEncoder
    ): FloconPreferencesPlugin {
        return FloconSharedPrefsPluginImpl(
            context = floconConfig.context,
            sender = floconConfig.client as FloconMessageSender,
            encoder = encoder,
        )
            .also { FloconSharedPrefsPluginImpl.plugin = it }
    }
}

internal interface FloconPreferencesDataSource {
    fun detectLocalPreferences(): List<FloconPreference>
}

internal expect fun buildFloconPreferencesDataSource(context: FloconContext): FloconPreferencesDataSource

internal class FloconSharedPrefsPluginImpl(
    context: FloconContext,
    private val sender: FloconMessageSender,
    private val encoder: FloconEncoder,
) : FloconPlugin, FloconPreferencesPlugin {
    override val key: String = Protocol.ToDevice.SharedPreferences.Plugin

    private val dataSource = buildFloconPreferencesDataSource(context)

    // references for quick access, by name
    private val preferences = MutableStateFlow<Map<String, FloconPreference>>(emptyMap())

    override suspend fun onMessageReceived(
        method: String,
        body: String,
    ) {
        when (method) {
            Protocol.ToDevice.SharedPreferences.Method.GetSharedPreferences -> {
                sendAllSharedPrefs()
            }

            Protocol.ToDevice.SharedPreferences.Method.GetSharedPreferenceValue -> {
                val message = encoder.decode<ToDeviceGetSharedPreferenceValueMessage>(body) ?: return
                val preference = preferences.value[message.sharedPreferenceName] ?: return

                sendPreferenceValues(
                    preference = preference,
                    requestId = message.requestId,
                )
            }

            Protocol.ToDevice.SharedPreferences.Method.SetSharedPreferenceValue -> {
                val message = encoder.decode<ToDeviceEditSharedPreferenceValueMessage>(body) ?: return
                val preference = preferences.value[message.sharedPreferenceName] ?: return

                try {
                    preference.set(
                        columnName = message.key,
                        value = FloconPreferenceValue(
                            stringValue = message.stringValue,
                            booleanValue = message.booleanValue,
                            intValue = message.intValue,
                            longValue = message.longValue,
                            floatValue = message.floatValue,
                            setStringValue = message.setStringValue,
                        ),
                    )

                    // then send the shared pref content
                    sendPreferenceValues(
                        preference = preference,
                        requestId = message.requestId,
                    )
                } catch (t: Throwable) {
                    FloconLogger.logError("SharedPreferences edit error", t)
                }
            }
        }
    }

    // on connected, send all shared prefs
    override suspend fun onConnectedToServer() {
        dataSource.detectLocalPreferences().forEach(::registerInternal)
        sendAllSharedPrefs()
    }

    override fun register(preference: FloconPreference) {
        registerInternal(preference)
        sendAllSharedPrefs()
    }

    private fun registerInternal(preference: FloconPreference) {
        preferences.update { current ->
            if (current.containsKey(preference.name)) current
            else current + (preference.name to preference)
        }
    }

    private suspend fun sendPreferenceValues(
        preference: FloconPreference,
        requestId: String,
    ) {
        val rows = preference.columns().map { key ->
            val value = preference.get(key)
            PreferenceRowDataModel(
                key = key,
                stringValue = value?.stringValue,
                intValue = value?.intValue,
                floatValue = value?.floatValue,
                booleanValue = value?.booleanValue,
                longValue = value?.longValue,
                setStringValue = value?.setStringValue,
            )
        }

        try {
            sender.send(
                plugin = Protocol.FromDevice.SharedPreferences.Plugin,
                method = Protocol.FromDevice.SharedPreferences.Method.GetSharedPreferenceValue,
                body = encoder.encode(
                    SharedPreferenceValueResultDataModel(
                        requestId = requestId,
                        sharedPreferenceName = preference.name,
                        rows = rows,
                    )
                ),
            )
        } catch (t: Throwable) {
            FloconLogger.logError("SharedPreferences json mapping error", t)
        }
    }

    private fun sendAllSharedPrefs() {
        val descriptors = preferences.value.values
            .sortedBy { it.name }
            .map { PreferencesDescriptor(it.name) }

        try {
            sender.send(
                plugin = Protocol.FromDevice.SharedPreferences.Plugin,
                method = Protocol.FromDevice.SharedPreferences.Method.GetSharedPreferences,
                body = encoder.encode<List<PreferencesDescriptor>>(descriptors),
            )
        } catch (t: Throwable) {
            FloconLogger.logError("SharedPreferences json mapping error", t)
        }
    }

    companion object {
        var plugin: FloconPreferencesPlugin? = null
    }
}

val Flocon.Companion.preferencesPlugin: FloconPreferencesPlugin
    get() = FloconSharedPrefsPluginImpl.plugin ?: pluginNotInitialized("Preferences", NoOpPreferencesPlugin())

private class NoOpPreferencesPlugin : FloconPreferencesPlugin {
    override val key: String = ""
    override fun register(preference: FloconPreference) = Unit
    override suspend fun onMessageReceived(method: String, body: String) = Unit
    override suspend fun onConnectedToServer() = Unit
}
