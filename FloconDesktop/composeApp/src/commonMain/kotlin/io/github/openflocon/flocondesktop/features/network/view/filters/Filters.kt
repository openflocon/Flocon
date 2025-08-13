package io.github.openflocon.flocondesktop.features.network.view.filters

import androidx.compose.runtime.Stable
import io.github.openflocon.flocondesktop.features.network.FilterUiState
import io.github.openflocon.flocondesktop.features.network.model.NetworkItemViewState

@Stable
interface Filters {

    fun filter(state: FilterUiState, list: List<NetworkItemViewState>): List<NetworkItemViewState>
}
