package io.github.openflocon.flocondesktop.device.models

import androidx.compose.runtime.Immutable

@Immutable
data class DeviceUiState(
    val deviceSerial: String,
    val contentState: ContentUiState
)

internal fun previewDeviceUiState() = DeviceUiState(
    deviceSerial = "",
    contentState = previewContentUiState()
)
