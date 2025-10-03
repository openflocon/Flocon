package io.github.openflocon.flocondesktop.features.network.list.processor

import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.domain.network.models.NetworkTextFilterColumns
import io.github.openflocon.flocondesktop.features.network.list.model.TopBarUiState
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkItemViewState
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkMethodUi
import io.github.openflocon.flocondesktop.features.network.list.model.header.columns.base.filter.TextFilterStateUiModel

class FilterNetworkItemsProcessor {
    operator fun invoke(
        items: List<Pair<FloconNetworkCallDomainModel, NetworkItemViewState>>,
        filterState: TopBarUiState,
        allowedMethods: List<NetworkMethodUi>,
        textFilters: Map<NetworkTextFilterColumns, TextFilterStateUiModel>,
    ): List<NetworkItemViewState> = items.asSequence()
        .filter { item ->
            item.second.method in allowedMethods
        }
        .map { it.second }
        .toList()
}
