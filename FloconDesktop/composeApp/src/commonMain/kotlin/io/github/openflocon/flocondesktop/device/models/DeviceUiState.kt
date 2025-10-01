package io.github.openflocon.flocondesktop.device.models

import androidx.compose.runtime.Immutable

@Immutable
data class DeviceUiState(
    val contentState: ContentUiState,
    val infoState: InfoUiState,
    val cpuState: CpuUiState,
    val memoryState: MemoryUiState,
    val permissionState: PermissionUiState
)

internal fun previewDeviceUiState() = DeviceUiState(
    contentState = previewContentUiState(),
    cpuState = previewCpuUiState(),
    memoryState = previewMemoryUiState(),
    infoState = previewInfoUiState(),
    permissionState = previewPermissionUiState()
)
