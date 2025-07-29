package com.github.openflocon.flocon.plugins.files.model.todevice

import com.github.openflocon.flocon.FloconLogger
import org.json.JSONObject

data class ToDeviceDeleteFolderContentMessage(
    val requestId: String,
    val path: String,
    val isConstantPath: Boolean, // ex: context.files / context.caches
) {
    companion object {
        fun fromJson(message: String): ToDeviceDeleteFolderContentMessage? {
            return try {
                val jsonObject = JSONObject(message)

                ToDeviceDeleteFolderContentMessage(
                    requestId = jsonObject.getString("requestId"),
                    path = jsonObject.getString("path"),
                    isConstantPath =  jsonObject.getBoolean("isConstantPath"),
                )
            }  catch (t: Throwable) {
                FloconLogger.logError("parsing issue", t)
                null
            }
        }
    }
}

