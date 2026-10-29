package io.github.openflocon.domain.device.models

data class DeviceCapabilitiesDomainModel(
    val screenshot: Boolean,
    val recordScreen: Boolean,
    val restart: Boolean,
    val files: Boolean,
    val sharedPreferences: Boolean,
    val deeplinks: Boolean,
    // false when adb can't reach the device (connected over wifi), so the ADB Commander is unusable
    val adbAvailable: Boolean = true,
)
