package io.github.openflocon.flocondesktop.features.network.list.model

sealed interface SortedByUiModel {
    sealed interface Enabled : SortedByUiModel {
        data object Ascending : Enabled
        data object Descending : Enabled
    }

    data object None : SortedByUiModel
}
