package io.github.openflocon.flocon.plugins.files.model.todevice

import io.github.openflocon.flocon.FloconLogger
import org.json.JSONObject

data class ToDeviceDeleteFileMessage(
    val requestId: String,
    val parentPath: String,
    val filePath: String,
    val isConstantParentPath: Boolean, // ex: context.files / context.caches
) {
    companion object {
        fun fromJson(message: String): ToDeviceDeleteFileMessage? {
            return try {
                val jsonObject = JSONObject(message)

                ToDeviceDeleteFileMessage(
                    requestId = jsonObject.getString("requestId"),
                    parentPath = jsonObject.getString("parentPath"),
                    filePath =  jsonObject.getString("filePath"),
                    isConstantParentPath =  jsonObject.getBoolean("isConstantParentPath"),
                )
            }  catch (t: Throwable) {
                FloconLogger.logError("parsing issue", t)
                null
            }
        }
    }
}