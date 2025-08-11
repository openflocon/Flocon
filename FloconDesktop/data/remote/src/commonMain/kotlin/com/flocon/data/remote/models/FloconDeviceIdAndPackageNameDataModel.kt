package com.flocon.data.remote.models

import io.github.openflocon.domain.models.DeviceId
import io.github.openflocon.domain.models.DeviceIdAndPackageNameDomainModel
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
