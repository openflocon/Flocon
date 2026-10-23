package io.github.openflocon.domain.device.models

enum class DeviceConnectionTypeDomainModel {
    // through `adb reverse`: the device reaches the desktop from the loopback interface
    Adb,

    // directly over the network (FloconConnectionMode.Url)
    Network,
}

fun isLoopbackAddress(address: String): Boolean = address == "localhost" ||
    address.startsWith("127.") ||
    address == "::1" ||
    address == "0:0:0:0:0:0:0:1"

fun connectionTypeOf(remoteAddress: String): DeviceConnectionTypeDomainModel = if (isLoopbackAddress(remoteAddress)) {
    DeviceConnectionTypeDomainModel.Adb
} else {
    DeviceConnectionTypeDomainModel.Network
}

/**
 * The connection type of each device: the live one when the device is connected, else the last one it used.
 */
fun mergeConnectionTypes(
    activeAddresses: Map<DeviceId, String>,
    lastTypes: Map<DeviceId, DeviceConnectionTypeDomainModel>,
): Map<DeviceId, DeviceConnectionTypeDomainModel> = lastTypes + activeAddresses.mapValues { (_, address) -> connectionTypeOf(address) }
