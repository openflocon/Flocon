package io.github.openflocon.flocondesktop.device.models

import androidx.compose.runtime.Immutable

@Immutable
data class ContentUiState(
    val selectedIndex: Int
)

fun previewContentUiState() = ContentUiState(
    selectedIndex = 0
)
