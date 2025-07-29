package com.github.openflocon.flocon.plugins.dashboard.model.todevice

import com.github.openflocon.flocon.FloconLogger
import org.json.JSONObject

data class ToDeviceCheckBoxValueChangedMessage(
    val id: String,
    val value: Boolean,
) {
    companion object {
        fun fromJson(message: String): ToDeviceCheckBoxValueChangedMessage? {
            return try {
                val jsonObject = JSONObject(message)

                ToDeviceCheckBoxValueChangedMessage(
                    id = jsonObject.getString("id"),
                    value = jsonObject.getBoolean("value"),
                )
            } catch (t: Throwable) {
                FloconLogger.logError("parsing issue", t)
                null
            }
        }
    }
}