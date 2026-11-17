package io.github.openflocon.flocondesktop.device

import io.github.openflocon.flocondesktop.device.DeviceTab.BATTERY
import io.github.openflocon.flocondesktop.device.DeviceTab.CPU
import io.github.openflocon.flocondesktop.device.DeviceTab.INFORMATION
import io.github.openflocon.flocondesktop.device.DeviceTab.MEMORY
import io.github.openflocon.flocondesktop.device.DeviceTab.PERMISSION

enum class DeviceTab {
    CPU,
    MEMORY,
    PERMISSION,
    INFORMATION,
    BATTERY
}

val DeviceTab.title: String
    get() = when (this) {
        INFORMATION -> "Info"
        BATTERY -> "Battery"
        CPU -> "CPU"
        MEMORY -> "Memory"
        PERMISSION -> "Permission"
    }
