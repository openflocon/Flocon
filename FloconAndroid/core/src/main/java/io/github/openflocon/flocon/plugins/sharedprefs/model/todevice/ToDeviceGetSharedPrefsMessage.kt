package io.github.openflocon.flocon.plugins.sharedprefs.model.todevice

import io.github.openflocon.flocon.FloconLogger
import org.json.JSONObject

data class ToDeviceGetSharedPrefsMessage(
    val requestId: String,
) {
    companion object {
        fun fromJson(message: String): ToDeviceGetSharedPrefsMessage? {
            return try {
                val jsonObject = JSONObject(message)

                val requestId = jsonObject.getString("requestId")

                ToDeviceGetSharedPrefsMessage(
                    requestId = requestId,
                )
            }  catch (t: Throwable) {
                FloconLogger.logError("parsing issue", t)
                null
            }
        }
    }
}