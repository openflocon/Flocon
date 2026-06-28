package io.github.openflocon.flocon.plugins.sharedprefs

import io.github.openflocon.flocon.FloconConfig
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.FloconPlugin
import io.github.openflocon.flocon.FloconPluginConfig
import io.github.openflocon.flocon.FloconPluginFactory
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.plugins.sharedprefs.model.FloconSharedPreferenceModel

class FloconPreferencesConfig : FloconPluginConfig

interface FloconPreferencesPlugin : FloconPlugin {
    fun register(sharedPreference: FloconSharedPreferenceModel)
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
            sender = floconConfig.client as FloconMessageSender
        )
    }
}

internal interface FloconSharedPreferenceDataSource {
    fun getSharedPreferences(): List<FloconSharedPreferenceModel>
    fun getSharedPreferenceValue(fileName: String, key: String): String?
    fun setSharedPreferenceValue(fileName: String, key: String, value: String)
}

internal expect fun buildFloconSharedPreferenceDataSource(context: FloconContext): FloconSharedPreferenceDataSource

internal class FloconSharedPrefsPluginImpl(
    private val context: FloconContext,
    private val sender: FloconMessageSender,
) : FloconPlugin, FloconPreferencesPlugin {
    override val key: String = "SHARED_PREF"

    private val dataSource = buildFloconSharedPreferenceDataSource(context)
    private val preferenceModels = mutableListOf<FloconSharedPreferenceModel>()

    override suspend fun onMessageReceived(
        method: String,
        body: String,
    ) {
//        when (method) {
//            Protocol.ToDevice.SharedPreferences.Method.GetSharedPreferences -> {
//                sendSharedPreferences()
//            }
//
//            Protocol.ToDevice.SharedPreferences.Method.GetSharedPreferenceValue -> {
//                // Not implemented yet on device side, usually handled by getSharedPreferences
//            }
//
//            Protocol.ToDevice.SharedPreferences.Method.SetSharedPreferenceValue -> {
//                SetSharedPreferenceValueMessage.fromJson(body)?.let { message ->
//                    dataSource.setSharedPreferenceValue(
//                        fileName = message.fileName,
//                        key = message.key,
//                        value = message.value
//                    )
//                    // Refresh view
//                    sendSharedPreferences()
//                }
//            }
//        }
    }

    override suspend fun onConnectedToServer() {
        sendSharedPreferences()
    }

    override fun register(sharedPreference: FloconSharedPreferenceModel) {
        preferenceModels.add(sharedPreference)
        sendSharedPreferences()
    }

    private fun sendSharedPreferences() {
        dataSource.getSharedPreferences() + preferenceModels
        try {
//            sender.send(
//                plugin = Protocol.FromDevice.SharedPreferences.Plugin,
//                method = Protocol.FromDevice.SharedPreferences.Method.GetSharedPreferences,
//                body = allPrefs.toJson().toString()
//            )
        } catch (t: Throwable) {
            FloconLogger.logError("SharedPreferences json mapping error", t)
        }
    }
}