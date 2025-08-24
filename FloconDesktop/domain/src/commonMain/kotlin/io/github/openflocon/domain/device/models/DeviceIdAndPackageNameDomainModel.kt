package io.github.openflocon.domain.device.models

data class DeviceIdAndPackageNameDomainModel(
    val deviceId: DeviceId,
    val packageName: AppPackageName,
    val appInstance: AppInstance,
)
