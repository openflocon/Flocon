package io.github.openflocon.flocondesktop.features.network.ui

import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.domain.network.models.NetworkTextFilterColumns
import io.github.openflocon.flocondesktop.features.network.ui.delegate.HeaderDelegate
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkItemViewState
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkMethodUi
import io.github.openflocon.flocondesktop.features.network.ui.model.SortedByUiModel
import io.github.openflocon.flocondesktop.features.network.ui.model.header.columns.NetworkColumnsTypeUiModel
import io.github.openflocon.flocondesktop.features.network.ui.model.header.columns.base.filter.TextFilterStateUiModel

class SortAndFilterNetworkItemsProcessor {
    operator fun invoke(
        items: List<Pair<FloconNetworkCallDomainModel, NetworkItemViewState>>,
        filterState: FilterUiState,
        sorted: HeaderDelegate.Sorted?,
        allowedMethods: List<NetworkMethodUi>,
        textFilters: Map<NetworkTextFilterColumns, TextFilterStateUiModel>,
    ): List<NetworkItemViewState> = items.asSequence()
        .filter { item ->
            (filterState.query.isEmpty() || item.second.contains(filterState.query))
        }
        .filter { item ->
            item.second.method in allowedMethods
        }
        .filter { item ->
            textFilters.filter { it.value.isActive }.all { (column, textFilter) ->
                textFilter.filter(column, item)
            }
        }
        .let {
            sort(it, sorted)
        }
        .map { it.second }
        .toList()
}

private fun sort(
    sequence: Sequence<Pair<FloconNetworkCallDomainModel, NetworkItemViewState>>,
    sorted: HeaderDelegate.Sorted?,
): Sequence<Pair<FloconNetworkCallDomainModel, NetworkItemViewState>> {
    if (sorted == null) {
        return sequence
    }

    val comparator = when (sorted.column) {
        NetworkColumnsTypeUiModel.RequestTime -> compareBy<Pair<FloconNetworkCallDomainModel, NetworkItemViewState>> { it.first.networkRequest.startTime }
        NetworkColumnsTypeUiModel.Method -> compareBy { it.second.method.text }
        NetworkColumnsTypeUiModel.Domain -> compareBy { it.second.domain }
        NetworkColumnsTypeUiModel.Query -> compareBy { it.second.type.text }
        NetworkColumnsTypeUiModel.Status -> compareBy { it.second.status.text }
        NetworkColumnsTypeUiModel.Time -> compareBy { it.first.networkResponse?.durationMs }
    }

    val sortedComparator = when (sorted.sort) {
        is SortedByUiModel.Enabled.Ascending -> comparator
        is SortedByUiModel.Enabled.Descending -> comparator.reversed()
    }

    return sequence.sortedWith(sortedComparator)
}

private fun TextFilterStateUiModel.filter(
    column: NetworkTextFilterColumns,
    items: List<Pair<FloconNetworkCallDomainModel, NetworkItemViewState>>,
): List<Pair<FloconNetworkCallDomainModel, NetworkItemViewState>> = items.filter { item ->
    filter(column, item)
}

private fun TextFilterStateUiModel.filter(
    column: NetworkTextFilterColumns,
    item: Pair<FloconNetworkCallDomainModel, NetworkItemViewState>,
): Boolean {
    val text = when (column) {
        NetworkTextFilterColumns.RequestTime -> item.second.dateFormatted
        NetworkTextFilterColumns.Domain -> item.second.domain
        NetworkTextFilterColumns.Query -> item.second.type.text
        NetworkTextFilterColumns.Status -> item.second.status.text
        NetworkTextFilterColumns.Time -> item.second.timeFormatted
    }
    return filterByText(text)
}

private fun TextFilterStateUiModel.filterByText(text: String?): Boolean {
    if(text == null)
        return true // accepts if text is null

    for (filter in this.allFilters) {
        if (!filter.filterByText(text))
            return false
    }

    return true
}

private fun TextFilterStateUiModel.FilterItem.filterByText(text: String): Boolean {
    if (!this.isActive)
        return true

    return if (this.isExcluded) {
        !text.contains(this.text, ignoreCase = true)
    } else {
        text.contains(this.text, ignoreCase = true)
    }
}
