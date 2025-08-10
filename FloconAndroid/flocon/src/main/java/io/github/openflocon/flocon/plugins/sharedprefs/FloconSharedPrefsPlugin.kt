package io.github.openflocon.flocon.plugins.SharedPreferences

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.core.FloconPlugin
import io.github.openflocon.flocon.model.FloconMessageFromServer
import io.github.openflocon.flocon.plugins.sharedprefs.SharedPreferencesFinder
import io.github.openflocon.flocon.plugins.sharedprefs.model.SharedPreferencesDescriptor
import io.github.openflocon.flocon.plugins.sharedprefs.model.fromdevice.SharedPreferenceRowDataModel
import io.github.openflocon.flocon.plugins.sharedprefs.model.fromdevice.SharedPreferenceValueResultDataModel
import io.github.openflocon.flocon.plugins.sharedprefs.model.listSharedPreferencesDescriptorToJson
import io.github.openflocon.flocon.plugins.sharedprefs.model.todevice.ToDeviceEditSharedPreferenceValueMessage
import io.github.openflocon.flocon.plugins.sharedprefs.model.todevice.ToDeviceGetSharedPreferenceValueMessage


// Got some code from Flipper client
// https://github.com/facebook/flipper/blob/main/android/src/main/java/com/facebook/flipper/plugins/sharedpreferences/SharedPreferencesFlipperPlugin.java

class FloconSharedPreferencesPluginImpl(
    private val context: Context,
) : FloconSharedPreferencesPlugin {

    private var sender: FloconMessageSender? = null

    private val mSharedPreferences: MutableMap<SharedPreferencesDescriptor, SharedPreferences> =
        mutableMapOf()
    private val mSharedPreferencesDescriptors: MutableMap<SharedPreferences, SharedPreferencesDescriptor> =
        mutableMapOf()

    private val onSharedPreferenceChangeListener: OnSharedPreferenceChangeListener =
        object : OnSharedPreferenceChangeListener {
            override fun onSharedPreferenceChanged(
                sharedPreferences: SharedPreferences,
                key: String?
            ) {
                if (sender == null) {
                    return
                }
                val descriptor: SharedPreferencesDescriptor? =
                    mSharedPreferencesDescriptors[sharedPreferences]
                if (descriptor == null) {
                    return
                }
                // TODO sender?.send(
                // TODO     "sharedPreferencesChange",
                // TODO     Builder()
                // TODO         .put("preferences", descriptor.name)
                // TODO         .put("name", key)
                // TODO         .put("deleted", !sharedPreferences.contains(key))
                // TODO         .put("time", System.currentTimeMillis())
                // TODO         .put("value", sharedPreferences.getAll().get(key))
                // TODO         .build()
                // TODO )
            }
        }

    override fun onMessageReceived(
        messageFromServer: FloconMessageFromServer,
        sender: FloconMessageSender,
    ) {
        this.sender = sender
        when (messageFromServer.method) {
            Protocol.ToDevice.SharedPreferences.Method.GetSharedPreferences -> {
                sendAllSharedPrefs(
                    sender = sender,
                )
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
                    setSharedPreferenceRowValue(
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
        val rows = getSharedPreferenceContent(
            sharedPreferencesName = sharedPreferenceName,
        )

        sender.send(
            plugin = Protocol.FromDevice.SharedPreferences.Plugin,
            method = Protocol.FromDevice.SharedPreferences.Method.GetSharedPreferenceValue,
            body = SharedPreferenceValueResultDataModel(
                requestId = requestId,
                sharedPreferenceName = sharedPreferenceName,
                rows = rows,
            ).toJson().toString(),
        )
    }

    // on connected, send all shared prefs
    override fun onConnectedToServer(sender: FloconMessageSender) {
        sendAllSharedPrefs(
            sender = sender,
        )
    }

    private fun sendAllSharedPrefs(
        sender: FloconMessageSender,
    ) {
        val allPrefs = getAllSharedPreferences()
        sender.send(
            plugin = Protocol.FromDevice.SharedPreferences.Plugin,
            method = Protocol.FromDevice.SharedPreferences.Method.GetSharedPreferences,
            body = listSharedPreferencesDescriptorToJson(allPrefs).toString(),
        )
    }


    private fun getSharedPreferenceContent(sharedPreferencesName: String): List<SharedPreferenceRowDataModel> {
        val sharedPreferences = getSharedPreferencesFor(sharedPreferencesName) ?: return emptyList()
        return sharedPreferences.all.mapNotNull {
            when (val value = it.value) {
                is String -> SharedPreferenceRowDataModel(
                    key = it.key,
                    stringValue = value,
                )

                is Int -> SharedPreferenceRowDataModel(
                    key = it.key,
                    intValue = value
                )

                is Boolean -> SharedPreferenceRowDataModel(
                    key = it.key,
                    booleanValue = value
                )

                is Float -> SharedPreferenceRowDataModel(
                    key = it.key,
                    floatValue = value
                )

                is Long -> SharedPreferenceRowDataModel(
                    key = it.key,
                    longValue = value
                )

                is Set<*> -> SharedPreferenceRowDataModel(
                    key = it.key,
                    setStringValue = value
                        .map { it.toString() }
                        .toSet()
                )

                else -> null // Ne mappe pas les types non reconnus ou null
            }
        }
    }

    private fun getAllSharedPreferences(): List<SharedPreferencesDescriptor> {
        val foundPrefs: List<SharedPreferencesDescriptor> =
            SharedPreferencesFinder.buildDescriptorForAllPrefsFiles(context)
        for (descriptor in foundPrefs) {
            if (mSharedPreferences.containsKey(descriptor)) {
                // no op
            } else {
                descriptor.getSharedPreferences(context)?.let { sharedPref ->
                    sharedPref.registerOnSharedPreferenceChangeListener(
                        onSharedPreferenceChangeListener
                    )
                    mSharedPreferences.put(descriptor, sharedPref)
                    mSharedPreferencesDescriptors.put(sharedPref, descriptor)
                }
            }
        }
        return foundPrefs
    }

    private fun getSharedPreferencesFor(name: String?): SharedPreferences? {
        for (entry in mSharedPreferencesDescriptors.entries) {
            if (entry.value.name == name) {
                return entry.key
            }
        }
        return null
    }

    @Throws(Throwable::class)
    private fun setSharedPreferenceRowValue(
        sharedPreferencesName: String,
        preferenceName: String,
        message: ToDeviceEditSharedPreferenceValueMessage,
    ) {
        val sharedPreferences = getSharedPreferencesFor(sharedPreferencesName) ?: return
        val originalValue: Any? = sharedPreferences.all[preferenceName]
        val editor = sharedPreferences.edit()

        if (originalValue is Boolean && message.booleanValue != null) {
            editor.putBoolean(preferenceName, message.booleanValue)
        } else if (originalValue is Long && message.longValue != null) {
            editor.putLong(preferenceName, message.longValue)
        } else if (originalValue is Int && message.intValue != null) {
            editor.putInt(preferenceName, message.intValue)
        } else if (originalValue is Float && message.floatValue != null) {
            editor.putFloat(preferenceName, message.floatValue)
        } else if (originalValue is String && message.stringValue != null) {
            editor.putString(preferenceName, message.stringValue)
        } else {
            throw IllegalArgumentException("Method not supported: " + preferenceName)
        }

        editor.apply()
    }
}