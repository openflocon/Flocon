package io.github.openflocon.flocondesktop.messages.domain.model

import com.flocon.data.remote.models.DeviceId
import com.flocon.data.remote.models.FloconDeviceIdAndPackageNameDataModel

data class DeviceIdAndPackageNameDomainModel(
    val deviceId: DeviceId,
    val packageName: String,
)

// TODO Move

fun DeviceIdAndPackageNameDomainModel.toRemote() = FloconDeviceIdAndPackageNameDataModel(
    deviceId = deviceId,
    packageName = packageName,
)
