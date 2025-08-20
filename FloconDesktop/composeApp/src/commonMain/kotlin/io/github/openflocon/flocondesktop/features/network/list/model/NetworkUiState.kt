package io.github.openflocon.flocondesktop.features.network.list.model

import androidx.compose.runtime.Immutable
import io.github.openflocon.flocondesktop.features.network.ContentUiState
import io.github.openflocon.flocondesktop.features.network.detail.model.NetworkDetailViewState
import io.github.openflocon.flocondesktop.features.network.list.model.header.NetworkHeaderUiState
import io.github.openflocon.flocondesktop.features.network.list.model.header.previewNetworkHeaderUiState
import io.github.openflocon.flocondesktop.features.network.previewContentUiState

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
