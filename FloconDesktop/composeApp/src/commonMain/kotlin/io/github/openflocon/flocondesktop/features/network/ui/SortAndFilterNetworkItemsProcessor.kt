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
        return items.asSequence()
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
}

fun sort(
    sequence: Sequence<Pair<FloconHttpRequestDomainModel, NetworkItemViewState>>,
    sorted: HeaderDelegate.Sorted?,
) : Sequence<Pair<FloconHttpRequestDomainModel, NetworkItemViewState>> {
    if (sorted == null) {
        return sequence
    }

    val comparator = when (sorted.column) {
        NetworkColumnsTypeUiModel.RequestTime -> compareBy<Pair<FloconHttpRequestDomainModel, NetworkItemViewState>> { it.first.request.startTime }
        NetworkColumnsTypeUiModel.Method -> compareBy { it.second.method.text }
        NetworkColumnsTypeUiModel.Domain -> compareBy { it.second.domain }
        NetworkColumnsTypeUiModel.Query -> compareBy { it.second.type.text }
        NetworkColumnsTypeUiModel.Status -> compareBy { it.second.status.text }
        NetworkColumnsTypeUiModel.Time -> compareBy { it.first.durationMs }
    }

    val sortedComparator = when (sorted.sort) {
        is SortedByUiModel.Enabled.Ascending -> comparator
        is SortedByUiModel.Enabled.Descending -> comparator.reversed()
    }

    return sequence.sortedWith(sortedComparator)
}

private fun TextFilterState.filter(
    column: TextFilterColumns,
    items: List<Pair<FloconHttpRequestDomainModel, NetworkItemViewState>>
): List<Pair<FloconHttpRequestDomainModel, NetworkItemViewState>> {
    return items.filter { item ->
        filter(column, item)
    }
}

private fun TextFilterState.filter(
    column: TextFilterColumns,
    item: Pair<FloconHttpRequestDomainModel, NetworkItemViewState>
): Boolean {
    val text = when (column) {
        TextFilterColumns.RequestTime -> item.second.dateFormatted
        TextFilterColumns.Domain -> item.second.domain
        TextFilterColumns.Query -> item.second.type.text
        TextFilterColumns.Status -> item.second.status.text
        TextFilterColumns.Time -> item.second.timeFormatted
    }
    return filterByText(text)
}

private fun TextFilterState.filterByText(text: String): Boolean {
    for (filter in this.allFilters) {
        if (!filter.filterByText(text))
            return false
    }

    return true
}

private fun TextFilterState.FilterItem.filterByText(text: String): Boolean {
    if (!this.isActive)
        return true

    return if (this.isExcluded) {
        !text.contains(this.text, ignoreCase = true)
    } else {
        text.contains(this.text, ignoreCase = true)
    }
}
