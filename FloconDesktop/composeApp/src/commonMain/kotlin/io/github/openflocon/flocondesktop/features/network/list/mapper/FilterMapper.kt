package io.github.openflocon.flocondesktop.features.network.list.mapper

import io.github.openflocon.domain.models.TextFilterStateDomainModel
import io.github.openflocon.domain.network.models.NetworkSortDomainModel
import io.github.openflocon.flocondesktop.features.network.list.delegate.HeaderDelegate
import io.github.openflocon.flocondesktop.features.network.list.model.SortedByUiModel
import io.github.openflocon.flocondesktop.features.network.list.model.header.columns.NetworkColumnsTypeUiModel
import io.github.openflocon.flocondesktop.features.network.list.model.header.columns.base.filter.TextFilterStateUiModel

fun toTextFilterUi(textFilter: TextFilterStateDomainModel): TextFilterStateUiModel = TextFilterStateUiModel(
    includedFilters = textFilter.items.filter { it.isExcluded.not() }.map { itemToUI(it) },
    excludedFilters = textFilter.items.filter { it.isExcluded }.map { itemToUI(it) },
    isEnabled = textFilter.isEnabled,
)

fun toTextFilterDomain(textFilter: TextFilterStateUiModel): TextFilterStateDomainModel = TextFilterStateDomainModel(
    items = textFilter.allFilters.map { itemToDomain(it) },
    isEnabled = textFilter.isEnabled,
)

fun itemToDomain(item: TextFilterStateUiModel.FilterItem): TextFilterStateDomainModel.FilterItem = TextFilterStateDomainModel.FilterItem(
    text = item.text,
    isActive = item.isActive,
    isExcluded = item.isExcluded,
    isRegex = item.isRegex,
)

fun itemToUI(item: TextFilterStateDomainModel.FilterItem): TextFilterStateUiModel.FilterItem = TextFilterStateUiModel.FilterItem(
    text = item.text,
    isActive = item.isActive,
    isExcluded = item.isExcluded,
    isRegex = item.isRegex,
)


internal fun HeaderDelegate.Sorted?.toDomain(): NetworkSortDomainModel? {
    val column = when(this?.column) {
        NetworkColumnsTypeUiModel.RequestTime -> NetworkSortDomainModel.Column.RequestStartTimeFormatted
        NetworkColumnsTypeUiModel.Method -> NetworkSortDomainModel.Column.Method
        NetworkColumnsTypeUiModel.Domain -> NetworkSortDomainModel.Column.Domain
        NetworkColumnsTypeUiModel.Query -> NetworkSortDomainModel.Column.Query
        NetworkColumnsTypeUiModel.Status -> NetworkSortDomainModel.Column.Status
        NetworkColumnsTypeUiModel.Time -> NetworkSortDomainModel.Column.Duration
        null -> return null
    }
    val asc = this.sort == SortedByUiModel.Enabled.Ascending
    return NetworkSortDomainModel(
        column = column,
        asc = asc,
    )
}
