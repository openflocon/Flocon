package io.github.openflocon.flocondesktop.features.network.list.model.header.columns.base

import androidx.compose.runtime.Immutable
import io.github.openflocon.flocondesktop.features.network.list.model.SortedByUiModel
import io.github.openflocon.flocondesktop.features.network.list.model.header.columns.base.filter.MethodFilterState

@Immutable
data class NetworkMethodColumnUiModel(
    override val sortedBy: SortedByUiModel,
    override val filter: MethodFilterState,
) : NetworkColumnUiModel {
    companion object {
        val EMPTY = NetworkMethodColumnUiModel(
            sortedBy = SortedByUiModel.None,
            filter = MethodFilterState(isEnabled = false, items = emptyList()),
        )
    }
}

fun previewNetworkMethodColumnUiModel() = NetworkMethodColumnUiModel(
    sortedBy = SortedByUiModel.None,
    filter = MethodFilterState(isEnabled = false, items = emptyList()),
)
