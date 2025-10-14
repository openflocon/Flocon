package io.github.openflocon.flocondesktop.features.network.list.model

import androidx.compose.runtime.Immutable

@Immutable
data class TopBarUiState(
    val displayOldSessions: Boolean,
    val hasMocks: Boolean,
    val hasWebsockets: Boolean,
    val hasBadNetwork: Boolean
)

fun previewTopBarUiState() = TopBarUiState(
    hasMocks = false,
    hasBadNetwork = false,
    displayOldSessions = true,
    hasWebsockets = false,
)
