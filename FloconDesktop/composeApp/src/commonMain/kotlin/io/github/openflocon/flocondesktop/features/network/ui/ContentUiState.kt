package io.github.openflocon.flocondesktop.features.network.ui

import androidx.compose.runtime.Immutable

@Immutable
data class ContentUiState(
    val selectedRequestId: String?
)

fun previewContentUiState() = ContentUiState(
    selectedRequestId = null
)
