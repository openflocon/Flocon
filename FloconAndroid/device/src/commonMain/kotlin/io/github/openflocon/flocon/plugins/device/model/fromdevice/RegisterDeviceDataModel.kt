package io.github.openflocon.flocon.plugins.device.model.fromdevice

import kotlinx.serialization.Serializable

@Serializable
internal class RegisterDeviceDataModel(
    val serial: String
)