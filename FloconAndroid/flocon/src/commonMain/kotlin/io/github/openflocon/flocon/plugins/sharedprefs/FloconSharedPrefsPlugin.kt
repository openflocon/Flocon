package io.github.openflocon.flocon.plugins.sharedprefs

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.core.FloconPlugin
import io.github.openflocon.flocon.model.FloconMessageFromServer
import io.github.openflocon.flocon.plugins.sharedprefs.model.SharedPreferencesDescriptor
import io.github.openflocon.flocon.plugins.sharedprefs.model.fromdevice.SharedPreferenceRowDataModel
import io.github.openflocon.flocon.plugins.sharedprefs.model.fromdevice.SharedPreferenceValueResultDataModel
import io.github.openflocon.flocon.plugins.sharedprefs.model.listSharedPreferencesDescriptorToJson
import io.github.openflocon.flocon.plugins.sharedprefs.model.todevice.ToDeviceEditSharedPreferenceValueMessage
import io.github.openflocon.flocon.plugins.sharedprefs.model.todevice.ToDeviceGetSharedPreferenceValueMessage

internal interface FloconSharedPreferencesDataSource {
    fun getAllSharedPreferences(): List<SharedPreferencesDescriptor>
    fun getSharedPreferenceContent(sharedPreferencesName: String): List<SharedPreferenceRowDataModel>
    fun setSharedPreferenceRowValue(
        sharedPreferencesName: String,
        preferenceName: String,
        message: ToDeviceEditSharedPreferenceValueMessage,
    )
}

internal expect fun buildFloconSharedPreferencesDataSource(context: FloconContext) : FloconSharedPreferencesDataSource

// Got some code from Flipper client
// https://github.com/facebook/flipper/blob/main/android/src/main/java/com/facebook/flipper/plugins/sharedpreferences/SharedPreferencesFlipperPlugin.java

internal class FloconSharedPreferencesPluginImpl(
    private val context: FloconContext,
    private var sender: FloconMessageSender,
) : FloconPlugin, FloconSharedPreferencesPlugin {

    private val dataSource = buildFloconSharedPreferencesDataSource(context)

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

                getSharedPreferenceValues(
                    sharedPreferenceName = toDeviceMessage.sharedPreferenceName,
                    requestId = toDeviceMessage.requestId,
                    sender = sender,
                )
            }

            Protocol.ToDevice.SharedPreferences.Method.SetSharedPreferenceValue -> {
                val toDeviceMessage =
                    ToDeviceEditSharedPreferenceValueMessage.fromJson(jsonString = messageFromServer.body)
                        ?: return
                try {
                    dataSource.setSharedPreferenceRowValue(
                        sharedPreferencesName = toDeviceMessage.sharedPreferenceName,
                        preferenceName = toDeviceMessage.key,
                        message = toDeviceMessage,
                    )

                    // then send the shared pref content
                    getSharedPreferenceValues(
                        sharedPreferenceName = toDeviceMessage.sharedPreferenceName,
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

    private fun getSharedPreferenceValues(
        sharedPreferenceName: String,
        requestId: String,
        sender: FloconMessageSender
    ) {
        val rows = dataSource.getSharedPreferenceContent(
            sharedPreferencesName = sharedPreferenceName,
        )

        try {
            sender.send(
                plugin = Protocol.FromDevice.SharedPreferences.Plugin,
                method = Protocol.FromDevice.SharedPreferences.Method.GetSharedPreferenceValue,
                body = SharedPreferenceValueResultDataModel(
                    requestId = requestId,
                    sharedPreferenceName = sharedPreferenceName,
                    rows = rows,
                ).toJson().toString(),
            )
        } catch (t: Throwable) {
            FloconLogger.logError("SharedPreferences json mapping error", t)
        }
    }

    // on connected, send all shared prefs
    override fun onConnectedToServer() {
        sendAllSharedPrefs()
    }

    private fun sendAllSharedPrefs() {
        val allPrefs = dataSource.getAllSharedPreferences()
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
}