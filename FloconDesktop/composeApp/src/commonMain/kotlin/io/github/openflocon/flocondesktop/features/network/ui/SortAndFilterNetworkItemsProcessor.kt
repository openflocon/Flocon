package io.github.openflocon.flocondesktop.features.network.ui

import io.github.openflocon.flocondesktop.features.network.domain.model.FloconHttpRequestDomainModel
import io.github.openflocon.flocondesktop.features.network.ui.delegate.HeaderDelegate
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkItemViewState
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkMethodUi
import io.github.openflocon.flocondesktop.features.network.ui.model.SortedByUiModel
import io.github.openflocon.flocondesktop.features.network.ui.model.header.columns.NetworkColumnsTypeUiModel
import io.github.openflocon.flocondesktop.features.network.ui.model.header.columns.base.filter.TextFilterColumns
import io.github.openflocon.flocondesktop.features.network.ui.model.header.columns.base.filter.TextFilterState

class SortAndFilterNetworkItemsProcessor {
    operator fun invoke(
        items: List<Pair<FloconHttpRequestDomainModel, NetworkItemViewState>>,
        filterState: FilterUiState,
        sorted: HeaderDelegate.Sorted?,
        allowedMethods: List<NetworkMethodUi>,
        textFilters: Map<TextFilterColumns, TextFilterState>,
    ): List<NetworkItemViewState> {
        var filteredItems = if (filterState.query.isNotEmpty())
            items.filter { it.second.contains(filterState.query) }
        else items

        filteredItems = filteredItems.filter {
            it.second.method in allowedMethods
        }

        textFilters.forEach { column, textFiler ->
            if(textFiler.isActive) {
                filteredItems = textFiler.filter(column, filteredItems)
            }
        }

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

fun TextFilterState.filter(
    column: TextFilterColumns,
    items: List<Pair<FloconHttpRequestDomainModel, NetworkItemViewState>>
) : List<Pair<FloconHttpRequestDomainModel, NetworkItemViewState>> {
    return items.filter { item ->
        val text = when(column) {
            TextFilterColumns.RequestTime -> item.second.dateFormatted
            TextFilterColumns.Domain -> item.second.domain
            TextFilterColumns.Query -> item.second.type.text
            TextFilterColumns.Time -> item.second.timeFormatted
        }
        filterByText(text)
    }
}

private fun TextFilterState.filterByText(text: String) : Boolean {
    for(filter in this.allFilters) {
        if(!filter.filterByText(text))
            return false
    }

    return true
}

private fun TextFilterState.FilterItem.filterByText(text: String) : Boolean {
    if(!this.isActive)
        return true

    return if(this.isExcluded) {
        !text.contains(this.text, ignoreCase = true)
    } else {
        text.contains(this.text, ignoreCase = true)
    }
}
