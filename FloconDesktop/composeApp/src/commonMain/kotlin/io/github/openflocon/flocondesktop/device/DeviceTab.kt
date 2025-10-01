package io.github.openflocon.flocondesktop.device

enum class DeviceTab {
    INFORMATION,
    BATTERY,
    CPU,
    MEMORY,
    PERMISSION
}

val DeviceTab.title: String
    get() = when (this) {
        DeviceTab.INFORMATION -> "Info"
        DeviceTab.BATTERY -> "Battery"
        DeviceTab.CPU -> "CPU"
        DeviceTab.MEMORY -> "Memory"
        DeviceTab.PERMISSION -> "Permission"
    }
