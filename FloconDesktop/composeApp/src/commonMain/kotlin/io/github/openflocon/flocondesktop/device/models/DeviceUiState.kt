package io.github.openflocon.flocondesktop.device.models

import androidx.compose.runtime.Immutable

@Immutable
data class DeviceUiState(
    val contentState: ContentUiState,
    val infoState: InfoUiState,
    val cpuState: CpuUiState,
    val memoryState: MemoryUiState,
    val permissionState: PermissionUiState,
    val batteryState: BatteryUiState
)

internal fun previewDeviceUiState() = DeviceUiState(
    contentState = previewContentUiState(),
    cpuState = previewCpuUiState(),
    memoryState = previewMemoryUiState(),
    infoState = previewInfoUiState(),
    permissionState = previewPermissionUiState(),
    batteryState = previewBatteryUiState()
)
