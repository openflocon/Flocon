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
        .filter { item ->
            val filters = textFilters.filter { it.value.isActive }
            if(filters.isEmpty()) true
            else {
                filters.any { (column, textFilter) ->
                    textFilter.filter(column, item)
                }
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
    // 1. If text is null, it always passes the filter (default behavior).
    if (text == null) {
        return true
    }

    val activeFilters = this.allFilters.filter { it.isActive }

    // 2. If there are no active filters, it always passes.
    if (activeFilters.isEmpty()) {
        return true
    }

    val (excludedFilters, includedFilters) = activeFilters.partition { it.isExcluded }

    // --- Step 1: Exclusion Check ---

    // 4. Check if the text matches ANY exclusion filter.
    // If ANY active 'isExcluded' filter matches, the item MUST be rejected (return false).
    val isExcluded = excludedFilters.any { it.matches(text) }

    if (isExcluded) {
        return false // Excluded, so it fails the filter immediately.
    }

    return true

    // --- Step 2: Inclusion Check ---

    //// 5. If there are no inclusion filters, the item passes (as it wasn't excluded).
    //if (includedFilters.isEmpty()) {
    //    return true
    //}
//
    //// 6. If not excluded, check if the text matches AT LEAST ONE inclusion filter.
    //// If ANY non-excluded filter matches, the item is kept (return true).
    //return includedFilters.any { it.matches(text) }
}

private fun TextFilterStateUiModel.FilterItem.matches(text: String): Boolean {
    return if (this.isRegex) {
        Regex(this.text, setOf(RegexOption.IGNORE_CASE)).containsMatchIn(text)
    } else {
        text.contains(this.text, ignoreCase = true)
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
