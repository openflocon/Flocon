package io.github.openflocon.flocon.plugins.sharedprefs

import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.core.FloconPlugin
import io.github.openflocon.flocon.model.FloconMessageFromServer
import io.github.openflocon.flocon.plugins.sharedprefs.model.FloconPreference
import io.github.openflocon.flocon.plugins.sharedprefs.model.FloconPreferenceValue
import io.github.openflocon.flocon.plugins.sharedprefs.model.fromdevice.PreferenceRowDataModel
import io.github.openflocon.flocon.plugins.sharedprefs.model.fromdevice.SharedPreferenceValueResultDataModel
import io.github.openflocon.flocon.plugins.sharedprefs.model.listSharedPreferencesDescriptorToJson
import io.github.openflocon.flocon.plugins.sharedprefs.model.todevice.ToDeviceEditSharedPreferenceValueMessage
import io.github.openflocon.flocon.plugins.sharedprefs.model.todevice.ToDeviceGetSharedPreferenceValueMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal interface FloconPreferencesDataSource {
    fun detectLocalPreferences(): List<FloconPreference>
}

internal expect fun buildFloconPreferencesDataSource(context: FloconContext): FloconPreferencesDataSource

internal class FloconPreferencesPluginImpl(
    context: FloconContext,
    private var sender: FloconMessageSender,
    private val scope: CoroutineScope,
) : FloconPlugin, FloconPreferencesPlugin {

    // references for quick access
    private val preferences = mutableMapOf<String, FloconPreference>()

    private val dataSource = buildFloconPreferencesDataSource(context)

    override fun onMessageReceived(
        messageFromServer: FloconMessageFromServer,
    ) {
        when (messageFromServer.method) {
            Protocol.ToDevice.SharedPreferences.Method.GetSharedPreferences -> {
                sendAllSharedPrefs()
            }

            Protocol.ToDevice.SharedPreferences.Method.GetSharedPreferenceValue -> {
                val toDeviceMessage =
                    ToDeviceGetSharedPreferenceValueMessage.fromJson(message = messageFromServer.body)
                        ?: return

                val preference = preferences[toDeviceMessage.sharedPreferenceName] ?: return

                scope.launch {
                    sendToServerPreferenceValues(
                        preference = preference,
                        requestId = toDeviceMessage.requestId,
                        sender = sender,
                    )
                }
            }

            Protocol.ToDevice.SharedPreferences.Method.SetSharedPreferenceValue -> {
                val toDeviceMessage =
                    ToDeviceEditSharedPreferenceValueMessage.fromJson(jsonString = messageFromServer.body)
                        ?: return

                val preference = preferences[toDeviceMessage.sharedPreferenceName] ?: return

                scope.launch {
                    try {
                        preference.set(
                            columnName = toDeviceMessage.key,
                            value = FloconPreferenceValue(
                                stringValue = toDeviceMessage.stringValue,
                                booleanValue = toDeviceMessage.booleanValue,
                                intValue = toDeviceMessage.intValue,
                                longValue = toDeviceMessage.longValue,
                                floatValue = toDeviceMessage.floatValue,
                                setStringValue = toDeviceMessage.setStringValue,
                            ),
                        )

                        // then send the shared pref content
                        sendToServerPreferenceValues(
                            preference = preference,
                            requestId = toDeviceMessage.requestId,
                            sender = sender,
                        )

                        //sender.send(Protocol.FromDevice.SharedPreferences.Plugin, "success")
                    } catch (t: Throwable) {
                        t.printStackTrace()
                        //sender.send(Protocol.FromDevice.SharedPreferences.Plugin, "failure")
                    }
                }
            }
        }
    }

    private suspend fun sendToServerPreferenceValues(
        preference: FloconPreference,
        requestId: String,
        sender: FloconMessageSender
    ) {
        val columns = preference.columns()
        val rows = columns.map { key ->
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
                body = SharedPreferenceValueResultDataModel(
                    requestId = requestId,
                    sharedPreferenceName = preference.name,
                    rows = rows,
                ).toJson(),
            )
        } catch (t: Throwable) {
            FloconLogger.logError("SharedPreferences json mapping error", t)
        }
    }

    // on connected, send all shared prefs
    override fun onConnectedToServer() {
        dataSource.detectLocalPreferences().forEach { preference ->
            registerInternal(preference)
        }
        sendAllSharedPrefs()
    }

    override fun register(preference: FloconPreference) {
        registerInternal(preference)
        sendAllSharedPrefs()
    }

    private fun registerInternal(preference: FloconPreference) {
        if(preferences.containsKey(preference.name).not()) {
            preferences[preference.name] = preference
        }
    }

    private fun sendAllSharedPrefs() {
        val allPrefs = getAllPreferences()
        try {
            sender.send(
                plugin = Protocol.FromDevice.SharedPreferences.Plugin,
                method = Protocol.FromDevice.SharedPreferences.Method.GetSharedPreferences,
                body = listSharedPreferencesDescriptorToJson(allPrefs).toString(),
            )
        } catch (t: Throwable) {
            FloconLogger.logError("SharedPreferences json mapping error", t)
        }
    }

    private fun getAllPreferences(): List<FloconPreference> {
        return preferences.values.sortedBy { it.name }
    }
}