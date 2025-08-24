package io.github.openflocon.domain.device.models

data class DeviceIdAndPackageNameDomainModel(
    val deviceId: DeviceId,
    val packageName: String,
    val appInstance: AppInstance,
)
