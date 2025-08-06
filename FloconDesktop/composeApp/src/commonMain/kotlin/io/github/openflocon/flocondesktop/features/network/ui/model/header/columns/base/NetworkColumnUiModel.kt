package io.github.openflocon.flocondesktop.features.network.ui.model.header.columns.base

import io.github.openflocon.flocondesktop.features.network.ui.model.SortedByUiModel

interface NetworkColumnUiModel {
    val sortedBy: SortedByUiModel
    val filter : FilterState
}

fun NetworkColumnUiModel.isFiltered() = filter.isActive
