package io.github.openflocon.domain.device.models

data class DeviceDomainModel(
    val deviceId: DeviceId,
    val deviceName: String,
    val platform: String,
    // how the SDK reached the desktop, sent in the bonjour message
    val connectionType: DeviceConnectionType = DeviceConnectionType.Default,
)

fun DeviceDomainModel.isPlatformAndroid(): Boolean = platform == "android"

fun DeviceDomainModel.isPlatformDesktop(): Boolean = platform == "desktop"

fun DeviceDomainModel.isPlatformIos(): Boolean = platform == "ios"

// adb needs the serial of the device, which is not available when it is connected over wifi
fun DeviceDomainModel.supportsAdb(): Boolean = connectionType != DeviceConnectionType.Wifi
