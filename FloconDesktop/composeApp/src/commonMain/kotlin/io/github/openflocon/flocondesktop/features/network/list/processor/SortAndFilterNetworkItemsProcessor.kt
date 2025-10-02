package io.github.openflocon.flocondesktop.features.network.list.processor

import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.domain.network.models.NetworkTextFilterColumns
import io.github.openflocon.flocondesktop.features.network.list.model.TopBarUiState
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkItemViewState
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkMethodUi
import io.github.openflocon.flocondesktop.features.network.list.model.header.columns.base.filter.TextFilterStateUiModel

class SortAndFilterNetworkItemsProcessor {
    operator fun invoke(
        items: List<Pair<FloconNetworkCallDomainModel, NetworkItemViewState>>,
        filterState: TopBarUiState,
        filterText: String,
        allowedMethods: List<NetworkMethodUi>,
        textFilters: Map<NetworkTextFilterColumns, TextFilterStateUiModel>,
    ): List<NetworkItemViewState> = items.asSequence()
        .filter { item ->
            (filterText.isEmpty() || item.second.contains(filterText) || item.first.contains(filterText))
        }
        .filter { item ->
            item.second.method in allowedMethods
        }
        .filter { item ->
            textFilters.filter { it.value.isActive }.all { (column, textFilter) ->
                textFilter.filter(column, item)
            }
        }
        .map { it.second }
        .toList()
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
