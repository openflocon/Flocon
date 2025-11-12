package io.github.openflocon.flocondesktop.device.models

import androidx.compose.runtime.Immutable

@Immutable
data class DeviceUiState(
    val deviceSerial: String,
    val contentState: ContentUiState,
    val infoState: InfoUiState,
    val cpuState: CpuUiState,
    val memoryState: MemoryUiState,
    val batteryState: BatteryUiState
)

internal fun previewDeviceUiState() = DeviceUiState(
    deviceSerial = "",
    contentState = previewContentUiState(),
    cpuState = previewCpuUiState(),
    memoryState = previewMemoryUiState(),
    infoState = previewInfoUiState(),
    batteryState = previewBatteryUiState()
)
