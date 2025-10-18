package io.github.openflocon.flocon.plugins.dashboard.model.todevice

import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.core.FloconEncoder
import kotlinx.serialization.Serializable

@Serializable
internal data class ToDeviceCheckBoxValueChangedMessage(
    val id: String,
    val value: Boolean,
) {
    companion object {
        fun fromJson(message: String): ToDeviceCheckBoxValueChangedMessage? {
            return try {
                FloconEncoder.json.decodeFromString<ToDeviceCheckBoxValueChangedMessage>(message)
            } catch (t: Throwable) {
                FloconLogger.logError("parsing issue", t)
                null
            }
        }
    }
}