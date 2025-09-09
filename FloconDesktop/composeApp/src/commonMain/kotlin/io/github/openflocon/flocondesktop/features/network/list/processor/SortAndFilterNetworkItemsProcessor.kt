package io.github.openflocon.flocondesktop.features.network.list.processor

import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.domain.network.models.NetworkTextFilterColumns
import io.github.openflocon.flocondesktop.features.network.list.delegate.HeaderDelegate
import io.github.openflocon.flocondesktop.features.network.list.model.FilterUiState
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkItemViewState
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkMethodUi
import io.github.openflocon.flocondesktop.features.network.list.model.SortedByUiModel
import io.github.openflocon.flocondesktop.features.network.list.model.header.columns.NetworkColumnsTypeUiModel
import io.github.openflocon.flocondesktop.features.network.list.model.header.columns.base.filter.TextFilterStateUiModel

class SortAndFilterNetworkItemsProcessor {
    operator fun invoke(
        items: List<Pair<FloconNetworkCallDomainModel, NetworkItemViewState>>,
        filterState: FilterUiState,
        sorted: HeaderDelegate.Sorted?,
        allowedMethods: List<NetworkMethodUi>,
        textFilters: Map<NetworkTextFilterColumns, TextFilterStateUiModel>,
    ): List<NetworkItemViewState> = items.asSequence()
        .filter { item ->
            (filterState.query.isEmpty() || item.second.contains(filterState.query) || item.first.contains(filterState.query))
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
        NetworkColumnsTypeUiModel.RequestTime -> compareBy<Pair<FloconNetworkCallDomainModel, NetworkItemViewState>> { it.first.request.startTime }
        NetworkColumnsTypeUiModel.Method -> compareBy { it.second.method.text }
        NetworkColumnsTypeUiModel.Domain -> compareBy { it.second.domain }
        NetworkColumnsTypeUiModel.Query -> compareBy { it.second.type.text }
        NetworkColumnsTypeUiModel.Status -> compareBy { it.second.status.text }
        NetworkColumnsTypeUiModel.Time -> compareBy { it.first.response?.durationMs }
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
    if (text == null)
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

    // Crée une instance de Regex en fonction de 'isRegex'
    val filterResult = if (this.isRegex) {
        Regex(this.text, setOf(RegexOption.IGNORE_CASE)).containsMatchIn(text)
    } else {
        text.contains(this.text, ignoreCase = true)
    }

    return if (this.isExcluded) {
        !filterResult
    } else {
        filterResult
    }
}

/**
 * lookup if the request or response body contains the text
 */
private fun FloconNetworkCallDomainModel.contains(text: String) : Boolean {
    return request.body?.contains(text, ignoreCase = true) == true || response?.contains(text) == true
}

private fun FloconNetworkCallDomainModel.Response.contains(text: String) : Boolean {
    return when(this) {
        is FloconNetworkCallDomainModel.Response.Failure -> issue.contains(text, ignoreCase = true)
        is FloconNetworkCallDomainModel.Response.Success -> body?.contains(text, ignoreCase = true) == true
    }
}
