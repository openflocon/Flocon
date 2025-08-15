package io.github.openflocon.flocondesktop.device

import androidx.compose.runtime.Immutable

@Immutable
data class DeviceUiState(
    val model: String,
    val brand: String,
    val versionRelease: String,
    val versionSdk: String,
    val serialNumber: String,
    val battery: String,
    val cpu: String,
    val mem: String
)

internal fun previewDeviceUiState() = DeviceUiState(
    model = "",
    brand = "",
    versionRelease = "",
    versionSdk = "",
    serialNumber = "",
    battery = "",
    cpu = "",
    mem = ""
)
