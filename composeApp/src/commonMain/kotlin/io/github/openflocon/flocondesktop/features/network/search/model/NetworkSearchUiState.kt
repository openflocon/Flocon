package io.github.openflocon.flocondesktop.features.network.search.model

import androidx.compose.runtime.Immutable
import io.github.openflocon.domain.network.models.SearchScope
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkItemViewState

@Immutable
data class NetworkSearchUiState(
    val selectedScopes: Set<SearchScope> = SearchScope.entries.toSet(),
    val results: List<NetworkItemViewState> = emptyList(),
    val loading: Boolean = false
)
