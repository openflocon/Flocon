package io.github.openflocon.flocon.plugins.files.model.todevice

import io.github.openflocon.flocon.FloconLogger
import org.json.JSONObject

internal data class ToDeviceGetFilesMessage(
    val requestId: String,
    val path: String,
    val isConstantPath: Boolean, // ex: context.files / context.caches
) {
    companion object {
        fun fromJson(message: String): ToDeviceGetFilesMessage? {
            return try {
                val jsonObject = JSONObject(message)

                ToDeviceGetFilesMessage(
                    requestId = jsonObject.getString("requestId"),
                    path = jsonObject.getString("path"),
                    isConstantPath = jsonObject.getBoolean("isConstantPath"),
                )
            }  catch (t: Throwable) {
                FloconLogger.logError("parsing issue", t)
                null
            }
        }
    }
}