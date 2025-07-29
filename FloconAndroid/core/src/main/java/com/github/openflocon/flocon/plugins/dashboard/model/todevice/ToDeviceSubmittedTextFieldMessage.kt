package com.github.openflocon.flocon.plugins.dashboard.model.todevice

import com.github.openflocon.flocon.FloconLogger
import org.json.JSONObject

data class ToDeviceSubmittedTextFieldMessage(
    val id: String,
    val value: String,
) {
    companion object {
        fun fromJson(message: String): ToDeviceSubmittedTextFieldMessage? {
            return try {
                val jsonObject = JSONObject(message)

                ToDeviceSubmittedTextFieldMessage(
                    id = jsonObject.getString("id"),
                    value = jsonObject.getString("value"),
                )
            }  catch (t: Throwable) {
                FloconLogger.logError("parsing issue", t)
                null
            }
        }
    }
}