package com.flocon.data.remote.models

import kotlinx.serialization.Serializable

// TODO Remove
typealias DeviceId = String

@Serializable
data class FloconDeviceIdAndPackageNameDataModel(
    val deviceId: DeviceId,
    val packageName: String,
)
