package io.github.openflocon.flocondesktop.features.network.ui

import androidx.compose.runtime.Immutable

@Immutable
data class FilterUiState(
    val query: String,
)

fun previewFilterUiState() = FilterUiState(
    query = "",
)
