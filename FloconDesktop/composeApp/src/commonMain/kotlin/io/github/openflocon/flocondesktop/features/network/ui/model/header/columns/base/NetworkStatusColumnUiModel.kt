package io.github.openflocon.flocondesktop.features.network.ui.model.header.columns.base

import androidx.compose.runtime.Immutable
import io.github.openflocon.flocondesktop.features.network.ui.model.SortedByUiModel
import io.github.openflocon.flocondesktop.features.network.ui.model.header.columns.base.filter.TextFilterState
import io.github.openflocon.flocondesktop.features.network.ui.model.header.columns.base.filter.previewTextFilterState

@Immutable
data class NetworkStatusColumnUiModel(
    override val sortedBy: SortedByUiModel,
    override val filter: TextFilterState, // TODO maybe later a specific filter
) : NetworkColumnUiModel {
    companion object {
        val EMPTY = NetworkStatusColumnUiModel(
            sortedBy = SortedByUiModel.None,
            filter = TextFilterState.EMPTY,
        )
    }
}

fun previewNetworkStatusColumnUiModel() = NetworkStatusColumnUiModel(
    sortedBy = SortedByUiModel.None,
    filter = previewTextFilterState(),
)
