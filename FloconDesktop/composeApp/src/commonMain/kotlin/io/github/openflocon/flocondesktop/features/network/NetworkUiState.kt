package io.github.openflocon.flocondesktop.features.network

import androidx.compose.runtime.Immutable
import io.github.openflocon.flocondesktop.features.network.model.NetworkDetailViewState
import io.github.openflocon.flocondesktop.features.network.model.NetworkItemViewState
import io.github.openflocon.flocondesktop.features.network.model.header.NetworkHeaderUiState
import io.github.openflocon.flocondesktop.features.network.model.header.previewNetworkHeaderUiState

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
