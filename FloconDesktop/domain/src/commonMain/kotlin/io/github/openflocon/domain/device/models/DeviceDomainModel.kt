package io.github.openflocon.domain.device.models

data class DeviceDomainModel(
    val deviceId: DeviceId,
    val deviceName: String,
    val platform: String,
)

fun DeviceDomainModel.isPlatformAndroid(): Boolean = platform == "android"

fun DeviceDomainModel.isPlatformDesktop(): Boolean = platform == "desktop"

fun DeviceDomainModel.isPlatformIos(): Boolean = platform == "ios"
