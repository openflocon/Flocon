package io.github.openflocon.flocondesktop.messages.domain.model

import io.github.openflocon.flocondesktop.DeviceId
import io.github.openflocon.flocondesktop.FloconDeviceIdAndPackageName

data class DeviceIdAndPackageNameDomainModel(
    val deviceId: DeviceId,
    val packageName: String,
)

// TODO Move

fun DeviceIdAndPackageNameDomainModel.toRemote() = FloconDeviceIdAndPackageName(
    deviceId = deviceId,
    packageName = packageName,
)
