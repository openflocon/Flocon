package io.github.openflocon.flocondesktop.features.network.ui

import androidx.compose.runtime.Immutable
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkDetailViewState
import io.github.openflocon.flocondesktop.features.network.ui.view.filters.MethodFilter

@Immutable
data class NetworkUiState(
    val contentUiState: ContentUiState = ContentUiState(),
    val detailState: NetworkDetailViewState? = null,
    val filterState: FilterUiState = FilterUiState()
)

@Immutable
data class ContentUiState(
    val selectedRequestId: String? = null
)

@Immutable
data class FilterUiState(
    val query: String = "",
    val methods: List<MethodFilter.Methods> = emptyList()
)
