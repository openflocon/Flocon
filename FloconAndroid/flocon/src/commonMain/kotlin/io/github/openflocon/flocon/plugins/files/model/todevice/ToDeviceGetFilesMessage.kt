package io.github.openflocon.flocon.plugins.files.model.todevice

import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.core.FloconEncoder
import kotlinx.serialization.Serializable

@Serializable
internal data class ToDeviceGetFilesMessage(
    val requestId: String,
    val path: String,
    val isConstantPath: Boolean, // true: the path is the id of a root folder
    val withFoldersSize: Boolean = false,
) {
    companion object {
        fun fromJson(message: String): ToDeviceGetFilesMessage? {
            return try {
                FloconEncoder.json.decodeFromString<ToDeviceGetFilesMessage>(message)

            }  catch (t: Throwable) {
                FloconLogger.logError("parsing issue", t)
                null
            }
        }
    }
}


