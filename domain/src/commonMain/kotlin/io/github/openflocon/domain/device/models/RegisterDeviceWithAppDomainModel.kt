package io.github.openflocon.domain.device.models

data class RegisterDeviceWithAppDomainModel(
    val device: DeviceDomainModel,
    val app: DeviceAppDomainModel,
) {
    val deviceIdAndPackageName = DeviceIdAndPackageNameDomainModel(
        deviceId = device.deviceId,
        packageName = app.packageName,
        appInstance = app.lastAppInstance,
    )
}
