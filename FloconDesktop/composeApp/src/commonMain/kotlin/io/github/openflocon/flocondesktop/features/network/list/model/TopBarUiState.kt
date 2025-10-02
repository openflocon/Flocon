package io.github.openflocon.flocondesktop.features.network.list.model

import androidx.compose.runtime.Immutable

@Immutable
data class TopBarUiState(
    val hasMocks: Boolean,
    val hasBadNetwork: Boolean
)

fun previewTopBarUiState() = TopBarUiState(
    hasMocks = false,
    hasBadNetwork = false
)
