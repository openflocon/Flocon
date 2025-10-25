package io.github.openflocon.domain.device.models

data class DeviceDomainModel(
    val deviceId: DeviceId,
    val deviceName: String,
    val platform: String,
)

fun DeviceDomainModel.isPlatformAndroid(): Boolean {
    return platform == "android"
}

fun DeviceDomainModel.isPlatformDesktop(): Boolean {
    return platform == "desktop"
}

fun DeviceDomainModel.isPlatformIos(): Boolean {
    return platform == "ios"
}
