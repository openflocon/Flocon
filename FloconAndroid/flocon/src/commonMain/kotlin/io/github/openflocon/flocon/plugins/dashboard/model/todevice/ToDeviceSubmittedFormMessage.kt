package io.github.openflocon.flocon.plugins.dashboard.model.todevice

import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.core.FloconEncoder
import kotlinx.serialization.Serializable

@Serializable
internal data class ToDeviceSubmittedFormMessage(
    val id: String,
    val values: Map<String, String>
) {
    companion object {
        fun fromJson(message: String): ToDeviceSubmittedFormMessage? {
            return try {
                FloconEncoder.json.decodeFromString<ToDeviceSubmittedFormMessage>(message)
            } catch (t: Throwable) {
                FloconLogger.logError("parsing issue", t)
                null
            }
        }
    }
}
