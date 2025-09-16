package io.github.openflocon.flocon.plugins.dashboard.model.todevice

import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.ext.toMap
import org.json.JSONObject

internal data class ToDeviceSubmittedFormMessage(
    val id: String,
    val values: Map<String, String>
) {
    companion object {
        fun fromJson(message: String): ToDeviceSubmittedFormMessage? {
            return try {
                val jsonObject = JSONObject(message)

                ToDeviceSubmittedFormMessage(
                    id = jsonObject.getString("id"),
                    values = jsonObject.getJSONObject("values").toMap()
                )
            } catch (t: Throwable) {
                FloconLogger.logError("parsing issue", t)
                null
            }
        }
    }
}
