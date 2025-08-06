package io.github.openflocon.flocondesktop.features.network.ui

import io.github.openflocon.flocondesktop.features.network.domain.model.FloconHttpRequestDomainModel
import io.github.openflocon.flocondesktop.features.network.ui.delegate.HeaderDelegate
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkItemViewState
import io.github.openflocon.flocondesktop.features.network.ui.model.SortedByUiModel
import io.github.openflocon.flocondesktop.features.network.ui.model.header.columns.NetworkColumnsTypeUiModel

class SortNetworkItemsProcessor {
    operator fun invoke(
        items: List<Pair<FloconHttpRequestDomainModel, NetworkItemViewState>>,
        filterState: FilterUiState,
        sorted: HeaderDelegate.Sorted?,
    ): List<NetworkItemViewState> {
        val filteredItems = if (filterState.query.isNotEmpty())
            items.filter { it.second.contains(filterState.query) }
        else items

        val sortedItems = if (sorted != null) {
            when (sorted.column) {
                NetworkColumnsTypeUiModel.RequestTime -> {
                    when (sorted.sort) {
                        SortedByUiModel.Enabled.Ascending -> filteredItems.sortedBy { it.first.request.startTime }
                        SortedByUiModel.Enabled.Descending -> filteredItems.sortedByDescending { it.first.request.startTime }
                    }
                }

                NetworkColumnsTypeUiModel.Method -> {
                    when (sorted.sort) {
                        SortedByUiModel.Enabled.Ascending -> filteredItems.sortedBy { it.second.method.text }
                        SortedByUiModel.Enabled.Descending -> filteredItems.sortedByDescending { it.second.method.text }
                    }
                }

                NetworkColumnsTypeUiModel.Domain -> {
                    when (sorted.sort) {
                        SortedByUiModel.Enabled.Ascending -> filteredItems.sortedBy { it.second.domain }
                        SortedByUiModel.Enabled.Descending -> filteredItems.sortedByDescending { it.second.domain }
                    }
                }

                NetworkColumnsTypeUiModel.Query -> {
                    when (sorted.sort) {
                        SortedByUiModel.Enabled.Ascending -> filteredItems.sortedBy { it.second.type.text }
                        SortedByUiModel.Enabled.Descending -> filteredItems.sortedByDescending { it.second.type.text }
                    }
                }

                NetworkColumnsTypeUiModel.Status -> when (sorted.sort) {
                    SortedByUiModel.Enabled.Ascending -> filteredItems.sortedBy { it.second.status.text }
                    SortedByUiModel.Enabled.Descending -> filteredItems.sortedByDescending { it.second.status.text }
                }

                NetworkColumnsTypeUiModel.Time -> {
                    when (sorted.sort) {
                        SortedByUiModel.Enabled.Ascending -> filteredItems.sortedBy { it.first.durationMs }
                        SortedByUiModel.Enabled.Descending -> filteredItems.sortedByDescending { it.first.durationMs }
                    }
                }
            }
        } else {
            filteredItems
        }


        return sortedItems.map { it.second }
    }
}
