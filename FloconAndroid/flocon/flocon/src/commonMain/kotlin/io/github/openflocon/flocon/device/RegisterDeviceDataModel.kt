package io.github.openflocon.flocon.device

import kotlinx.serialization.Serializable

@Serializable
internal class RegisterDeviceDataModel(
    val serial: String
)