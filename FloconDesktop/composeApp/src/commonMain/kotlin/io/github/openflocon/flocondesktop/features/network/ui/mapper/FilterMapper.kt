package io.github.openflocon.flocondesktop.features.network.ui.mapper

import io.github.openflocon.flocondesktop.features.network.domain.model.TextFilterStateDomainModel
import io.github.openflocon.flocondesktop.features.network.ui.model.header.columns.base.filter.TextFilterStateUiModel

fun toTextFilterUi(textFilter: TextFilterStateDomainModel) : TextFilterStateUiModel {
    return TextFilterStateUiModel(
        includedFilters = textFilter.items.filter { it.isExcluded.not() }.map { itemToUI(it) },
        excludedFilters = textFilter.items.filter { it.isExcluded }.map { itemToUI(it) },
        isEnabled = textFilter.isEnabled
    )
}

fun toTextFilterDomain(textFilter: TextFilterStateUiModel) : TextFilterStateDomainModel {
    return TextFilterStateDomainModel(
        items = textFilter.allFilters.map { itemToDomain(it) },
        isEnabled = textFilter.isEnabled,
    )
}

fun itemToDomain(item: TextFilterStateUiModel.FilterItem): TextFilterStateDomainModel.FilterItem {
    return TextFilterStateDomainModel.FilterItem(
        text = item.text,
        isActive = item.isActive,
        isExcluded = item.isExcluded
    )
}

fun itemToUI(item: TextFilterStateDomainModel.FilterItem): TextFilterStateUiModel.FilterItem {
    return TextFilterStateUiModel.FilterItem(
        text = item.text,
        isActive = item.isActive,
        isExcluded = item.isExcluded
    )
}
