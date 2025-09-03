package io.github.openflocon.flocondesktop.features.network.list.model

import androidx.compose.runtime.Immutable

@Immutable
data class FilterUiState(
    val query: String,
    val hasMocks: Boolean,
    val hasBadNetwork: Boolean
)

fun previewFilterUiState() = FilterUiState(
    query = "",
    hasMocks = false,
    hasBadNetwork = false
)
