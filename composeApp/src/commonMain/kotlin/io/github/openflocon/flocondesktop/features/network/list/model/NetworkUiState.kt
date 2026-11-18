package io.github.openflocon.flocondesktop.features.network.list.model

import androidx.compose.runtime.Immutable
import io.github.openflocon.flocondesktop.features.network.body.model.ContentUiState
import io.github.openflocon.flocondesktop.features.network.body.model.previewContentUiState
import io.github.openflocon.flocondesktop.features.network.list.model.header.NetworkHeaderUiState
import io.github.openflocon.flocondesktop.features.network.list.model.header.previewNetworkHeaderUiState

@Immutable
data class NetworkUiState(
    val showEmptyState: Boolean,
    val contentState: ContentUiState,
    val settings: NetworkSettingsUiModel,
    val filterState: FilterUiState,
    val headerState: NetworkHeaderUiState
)

fun previewNetworkUiState() = NetworkUiState(
    showEmptyState = false,
    contentState = previewContentUiState(),
    filterState = previewTopBarUiState(),
    headerState = previewNetworkHeaderUiState(),
    settings = previewNetworkSettingsUiModel(),
)
