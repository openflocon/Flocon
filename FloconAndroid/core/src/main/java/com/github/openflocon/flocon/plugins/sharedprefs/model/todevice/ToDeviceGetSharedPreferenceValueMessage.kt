package com.github.openflocon.flocon.plugins.sharedprefs.model.todevice

import com.github.openflocon.flocon.FloconLogger
import org.json.JSONObject

data class ToDeviceGetSharedPreferenceValueMessage(
    val requestId: String,
    val sharedPreferenceName: String,
) {
    companion object {
        fun fromJson(message: String): ToDeviceGetSharedPreferenceValueMessage? {
            return try {
                val jsonObject = JSONObject(message)

                ToDeviceGetSharedPreferenceValueMessage(
                    requestId = jsonObject.getString("requestId"),
                    sharedPreferenceName = jsonObject.getString("sharedPreferenceName"),
                )
            }  catch (t: Throwable) {
                FloconLogger.logError("parsing issue", t)
                null
            }
        }
    }
}