package io.github.openflocon.flocon.plugins.files.model.todevice

import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.core.FloconEncoder
import kotlinx.serialization.Serializable

@Serializable
internal data class ToDeviceDeleteFileMessage(
    val requestId: String,
    val parentPath: String,
    val filePath: String,
    val isConstantParentPath: Boolean, // true: the path is the id of a root folder
) {
    companion object {
        fun fromJson(message: String): ToDeviceDeleteFileMessage? {
            return try {
                FloconEncoder.json.decodeFromString<ToDeviceDeleteFileMessage>(message)
            } catch (t: Throwable) {
                FloconLogger.logError("parsing issue", t)
                null
            }
        }
    }
}

