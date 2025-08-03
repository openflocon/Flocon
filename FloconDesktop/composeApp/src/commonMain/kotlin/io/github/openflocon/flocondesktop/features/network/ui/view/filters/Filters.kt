package io.github.openflocon.flocondesktop.features.network.ui.view.filters

import androidx.compose.runtime.Stable
import io.github.openflocon.flocondesktop.features.network.domain.model.FloconHttpRequestDomainModel
import io.github.openflocon.flocondesktop.features.network.ui.FilterUiState

@Stable
interface Filters {

    fun filter(state: FilterUiState, list: List<FloconHttpRequestDomainModel>): List<FloconHttpRequestDomainModel>

}
