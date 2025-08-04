package io.github.openflocon.flocondesktop.features.network.ui.view.filters

import androidx.compose.runtime.Stable
import io.github.openflocon.flocondesktop.features.network.ui.FilterUiState
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkItemViewState

@Stable
interface Filters {

    fun filter(state: FilterUiState, list: List<NetworkItemViewState>): List<NetworkItemViewState>

}
