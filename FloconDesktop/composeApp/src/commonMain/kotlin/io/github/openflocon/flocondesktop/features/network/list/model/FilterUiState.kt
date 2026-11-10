package io.github.openflocon.flocondesktop.features.network.list.model

import androidx.compose.runtime.Immutable

@Immutable
data class FilterUiState(
    val displayOldSessions: Boolean,
    val hasMocks: Boolean,
    val hasWebsockets: Boolean,
    val hasBadNetwork: Boolean
)

fun previewTopBarUiState() = FilterUiState(
    hasMocks = false,
    hasBadNetwork = false,
    displayOldSessions = true,
    hasWebsockets = false,
)
