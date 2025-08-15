package com.flocon.data.remote.device.model

import kotlinx.serialization.Serializable

@Serializable
class RegisterDeviceDataModel(
    val serial: String? = null,
)
