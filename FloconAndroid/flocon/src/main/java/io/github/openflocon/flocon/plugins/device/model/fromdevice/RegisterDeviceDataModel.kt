package io.github.openflocon.flocon.plugins.device.model.fromdevice

import io.github.openflocon.flocon.core.FloconEncoder
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString

@Serializable
internal class RegisterDeviceDataModel(
    val serial: String,
) {
    fun toJson(): String {
        return FloconEncoder.json.encodeToString<RegisterDeviceDataModel>(this)
    }
}