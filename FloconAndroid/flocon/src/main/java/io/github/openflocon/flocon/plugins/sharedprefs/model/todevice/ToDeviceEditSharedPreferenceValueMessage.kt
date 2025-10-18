package io.github.openflocon.flocon.plugins.sharedprefs.model.todevice

import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.core.FloconEncoder
import kotlinx.serialization.Serializable

@Serializable
internal data class ToDeviceEditSharedPreferenceValueMessage(
    val requestId: String,
    val sharedPreferenceName: String,
    val key: String,
    val stringValue: String? = null,
    val intValue: Int? = null,
    val floatValue: Float? = null,
    val booleanValue: Boolean? = null,
    val longValue: Long? = null,
    val setStringValue: Set<String>? = null,
) {
    companion object {
        fun fromJson(jsonString: String): ToDeviceEditSharedPreferenceValueMessage? {
            return try {
                FloconEncoder.json.decodeFromString(jsonString)
            }  catch (t: Throwable) {
                FloconLogger.logError("parsing issue", t)
                null
            }
        }
    }
}
