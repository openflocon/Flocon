package io.github.openflocon.flocondesktop.features.network.ui.model.header.columns.base

import io.github.openflocon.flocondesktop.features.network.ui.model.SortedByUiModel
import io.github.openflocon.flocondesktop.features.network.ui.model.header.columns.base.filter.FilterState

interface NetworkColumnUiModel {
    val sortedBy: SortedByUiModel
    val filter: FilterState
}

fun NetworkColumnUiModel.isFiltered() = filter.isActive
