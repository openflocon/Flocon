package io.github.openflocon.flocondesktop.features.network.list.model

import androidx.compose.runtime.Immutable
import io.github.openflocon.flocondesktop.features.network.body.model.ContentUiState
import io.github.openflocon.flocondesktop.features.network.detail.model.NetworkDetailViewState
import io.github.openflocon.flocondesktop.features.network.list.model.header.NetworkHeaderUiState
import io.github.openflocon.flocondesktop.features.network.list.model.header.previewNetworkHeaderUiState
import io.github.openflocon.flocondesktop.features.network.body.model.previewContentUiState

@Immutable
data class NetworkUiState(
    val items: List<NetworkItemViewState>,
    val contentState: ContentUiState,
    val detailState: NetworkDetailViewState?,
    val filterState: TopBarUiState,
    val headerState: NetworkHeaderUiState,
)

fun previewNetworkUiState() = NetworkUiState(
    items = emptyList(),
    detailState = null,
    contentState = previewContentUiState(),
    filterState = previewTopBarUiState(),
    headerState = previewNetworkHeaderUiState(),
)
