package com.flocon.data.remote.models

import com.flocon.library.domain.models.DeviceId
import com.flocon.library.domain.models.DeviceIdAndPackageNameDomainModel
import kotlinx.serialization.Serializable

// TODO Set internal
@Serializable
data class FloconDeviceIdAndPackageNameDataModel(
    val deviceId: DeviceId,
    val packageName: String,
)

fun DeviceIdAndPackageNameDomainModel.toRemote() = FloconDeviceIdAndPackageNameDataModel(
    deviceId = deviceId,
    packageName = packageName,
)
