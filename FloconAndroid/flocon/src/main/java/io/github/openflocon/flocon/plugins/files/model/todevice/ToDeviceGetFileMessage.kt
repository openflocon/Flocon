package io.github.openflocon.flocon.plugins.files.model.todevice

import io.github.openflocon.flocon.FloconLogger
import org.json.JSONObject

internal data class ToDeviceGetFileMessage(
    val requestId: String,
    val path: String,
) {
    companion object {
        fun fromJson(message: String): ToDeviceGetFileMessage? {
            return try {
                val jsonObject = JSONObject(message)

                ToDeviceGetFileMessage(
                    requestId = jsonObject.getString("requestId"),
                    path = jsonObject.getString("path"),
                )
            }  catch (t: Throwable) {
                FloconLogger.logError("parsing issue", t)
                null
            }
        }
    }
}