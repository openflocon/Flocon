package io.github.openflocon.flocon.plugins.sharedprefs.model.todevice

import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.core.FloconEncoder
import kotlinx.serialization.Serializable

@Serializable
internal data class ToDeviceGetSharedPrefsMessage(
    val requestId: String,
) {
    companion object {
        fun fromJson(message: String): ToDeviceGetSharedPrefsMessage? {
            return try {
                FloconEncoder.json.decodeFromString(message)
            } catch (t: Throwable) {
                FloconLogger.logError("parsing issue", t)
                null
            }
        }
    }
}