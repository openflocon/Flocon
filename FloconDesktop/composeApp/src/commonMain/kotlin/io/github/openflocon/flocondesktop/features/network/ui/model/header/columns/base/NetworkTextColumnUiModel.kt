package io.github.openflocon.flocondesktop.features.network.ui.model.header.columns.base

import androidx.compose.runtime.Immutable
import io.github.openflocon.flocondesktop.features.network.ui.model.SortedByUiModel
import io.github.openflocon.flocondesktop.features.network.ui.model.header.columns.base.filter.TextFilterState
import io.github.openflocon.flocondesktop.features.network.ui.model.header.columns.base.filter.previewTextFilterState

@Immutable
data class NetworkTextColumnUiModel(
    override val sortedBy: SortedByUiModel,
    override val filter: TextFilterState,
) : NetworkColumnUiModel {
    companion object {
        val EMPTY = NetworkTextColumnUiModel(
            sortedBy = SortedByUiModel.None,
            filter = TextFilterState.EMPTY,
        )
    }
}

fun previewNetworkTextColumnUiModel() = NetworkTextColumnUiModel(
    sortedBy = SortedByUiModel.None,
    filter = previewTextFilterState(),
)
