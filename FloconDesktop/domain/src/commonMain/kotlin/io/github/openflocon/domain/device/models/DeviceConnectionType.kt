package io.github.openflocon.domain.device.models

enum class DeviceConnectionType {
    /** The default address: localhost (adb reverse, simulator). */
    Default,

    /** The address given to the connect module of the SDK. */
    Wifi,
}
