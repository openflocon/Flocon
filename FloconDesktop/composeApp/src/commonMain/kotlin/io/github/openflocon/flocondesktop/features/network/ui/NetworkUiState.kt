package io.github.openflocon.flocondesktop.features.network.ui

import androidx.compose.runtime.Immutable
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkDetailViewState
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkItemViewState
import io.github.openflocon.flocondesktop.features.network.ui.view.filters.MethodFilter

@Immutable
data class NetworkUiState(
    val items: List<NetworkItemViewState>,
    val contentState: ContentUiState,
    val detailState: NetworkDetailViewState?,
    val filterState: FilterUiState
)

@Immutable
data class ContentUiState(
    val selectedRequestId: String?
)

@Immutable
data class FilterUiState(
    val query: String,
    val methods: List<MethodFilter.Methods>
)

fun previewNetworkUiState() = NetworkUiState(
    items = emptyList(),
    detailState = null,
    contentState = previewContentUiState(),
    filterState = previewFilterUiState()
)

fun previewContentUiState() = ContentUiState(
    selectedRequestId = null
)

fun previewFilterUiState() = FilterUiState(
    query = "",
    methods = emptyList()
)
