package com.flocon.data.remote.models

import kotlinx.serialization.Serializable

@Serializable
data class FloconDeviceIdAndPackageNameDataModel(
    val deviceId: DeviceId,
    val packageName: String,
)
