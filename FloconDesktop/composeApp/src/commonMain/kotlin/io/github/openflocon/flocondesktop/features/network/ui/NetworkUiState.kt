package io.github.openflocon.flocondesktop.features.network.ui

import androidx.compose.runtime.Immutable
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkDetailViewState
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkItemViewState
import io.github.openflocon.flocondesktop.features.network.ui.model.header.NetworkHeaderUiState
import io.github.openflocon.flocondesktop.features.network.ui.model.header.previewNetworkHeaderUiState

@Immutable
data class NetworkUiState(
    val items: List<NetworkItemViewState>,
    val contentState: ContentUiState,
    val detailState: NetworkDetailViewState?,
    val filterState: FilterUiState,
    val headerState: NetworkHeaderUiState,
)

fun previewNetworkUiState() = NetworkUiState(
    items = emptyList(),
    detailState = null,
    contentState = previewContentUiState(),
    filterState = previewFilterUiState(),
    headerState = previewNetworkHeaderUiState(),
)
