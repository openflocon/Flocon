package io.github.openflocon.flocon.plugins.dashboard.model.todevice

import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.core.FloconEncoder
import kotlinx.serialization.Serializable

@Serializable
internal data class ToDeviceSubmittedTextFieldMessage(
    val id: String,
    val value: String,
) {
    companion object {
        fun fromJson(message: String): ToDeviceSubmittedTextFieldMessage? {
            return try {
                FloconEncoder.json.decodeFromString<ToDeviceSubmittedTextFieldMessage>(message)
            }  catch (t: Throwable) {
                FloconLogger.logError("parsing issue", t)
                null
            }
        }
    }
}