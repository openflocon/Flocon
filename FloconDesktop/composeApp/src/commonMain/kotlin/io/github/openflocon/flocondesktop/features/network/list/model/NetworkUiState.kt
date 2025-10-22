package io.github.openflocon.flocondesktop.features.network.list.model

import androidx.compose.runtime.Immutable
import io.github.openflocon.flocondesktop.features.network.body.model.ContentUiState
import io.github.openflocon.flocondesktop.features.network.body.model.previewContentUiState
import io.github.openflocon.flocondesktop.features.network.detail.model.NetworkDetailViewState
import io.github.openflocon.flocondesktop.features.network.list.model.header.NetworkHeaderUiState
import io.github.openflocon.flocondesktop.features.network.list.model.header.previewNetworkHeaderUiState

@Immutable
data class NetworkUiState(
    val contentState: ContentUiState,
    val settings: NetworkSettingsUiModel,
    val detailState: NetworkDetailViewState?,
    val filterState: TopBarUiState,
    val headerState: NetworkHeaderUiState
)

fun previewNetworkUiState() = NetworkUiState(
    detailState = null,
    contentState = previewContentUiState(),
    filterState = previewTopBarUiState(),
    headerState = previewNetworkHeaderUiState(),
    settings = previewNetworkSettingsUiModel(),
)
