package io.github.openflocon.flocon.plugins.device.model.fromdevice

import org.json.JSONObject

internal class RegisterDeviceDataModel(
    val serial: String,
) {
    fun toJson(): JSONObject {
        val json = JSONObject()

        json.put("serial", serial)

        return json
    }
}