package io.github.openflocon.flocondesktop.device.models

import androidx.compose.runtime.Immutable
import io.github.openflocon.flocondesktop.device.DeviceTab

@Immutable
data class ContentUiState(
    val selectedTab: DeviceTab
)

fun previewContentUiState() = ContentUiState(
    selectedTab = DeviceTab.INFORMATION
)
