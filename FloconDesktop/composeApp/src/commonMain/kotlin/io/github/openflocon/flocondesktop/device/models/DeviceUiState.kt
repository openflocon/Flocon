package io.github.openflocon.flocondesktop.device.models

import androidx.compose.runtime.Immutable

@Immutable
data class DeviceUiState(
    val deviceSerial: String,
    val contentState: ContentUiState,
    val infoState: InfoUiState,
    val memoryState: MemoryUiState
)

internal fun previewDeviceUiState() = DeviceUiState(
    deviceSerial = "",
    contentState = previewContentUiState(),
    memoryState = previewMemoryUiState(),
    infoState = previewInfoUiState()
)
