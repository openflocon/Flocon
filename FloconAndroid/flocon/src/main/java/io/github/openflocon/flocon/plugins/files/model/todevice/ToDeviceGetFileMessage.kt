package io.github.openflocon.flocon.plugins.files.model.todevice

import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.core.FloconEncoder
import kotlinx.serialization.Serializable

@Serializable
internal data class ToDeviceGetFileMessage(
    val requestId: String,
    val path: String,
) {
    companion object {
        fun fromJson(message: String): ToDeviceGetFileMessage? {
            return try {
                FloconEncoder.json.decodeFromString<ToDeviceGetFileMessage>(message)
            } catch (t: Throwable) {
                FloconLogger.logError("parsing issue", t)
                null
            }
        }
    }
}