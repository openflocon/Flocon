package io.github.openflocon.flocon.plugins.files.model.todevice

import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.core.FloconEncoder
import kotlinx.serialization.Serializable

@Serializable
internal data class ToDeviceDeleteFilesMessage(
    val requestId: String,
    val parentPath: String,
    val filePaths: List<String>,
    val isConstantParentPath: Boolean, // ex: context.files / context.caches
) {
    companion object {
        fun fromJson(message: String): ToDeviceDeleteFilesMessage? {
            return try {
                FloconEncoder.json.decodeFromString<ToDeviceDeleteFilesMessage>(message)
            } catch (t: Throwable) {
                FloconLogger.logError("parsing issue", t)
                null
            }
        }
    }
}