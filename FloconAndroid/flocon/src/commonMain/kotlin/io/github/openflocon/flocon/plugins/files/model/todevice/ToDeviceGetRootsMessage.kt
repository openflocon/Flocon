package io.github.openflocon.flocon.plugins.files.model.todevice

import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.core.FloconEncoder
import kotlinx.serialization.Serializable

@Serializable
internal data class ToDeviceGetRootsMessage(
    val requestId: String,
) {
    companion object {
        fun fromJson(message: String): ToDeviceGetRootsMessage? {
            return try {
                FloconEncoder.json.decodeFromString<ToDeviceGetRootsMessage>(message)
            } catch (t: Throwable) {
                FloconLogger.logError("parsing issue", t)
                null
            }
        }
    }
}
