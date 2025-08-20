package io.github.openflocon.flocondesktop.features.network.list.model.header.columns.base

import androidx.compose.runtime.Immutable
import io.github.openflocon.flocondesktop.features.network.list.model.SortedByUiModel
import io.github.openflocon.flocondesktop.features.network.list.model.header.columns.base.filter.TextFilterStateUiModel
import io.github.openflocon.flocondesktop.features.network.list.model.header.columns.base.filter.previewTextFilterState

@Immutable
data class NetworkTextColumnUiModel(
    override val sortedBy: SortedByUiModel,
    override val filter: TextFilterStateUiModel,
) : NetworkColumnUiModel {
    companion object {
        val EMPTY = NetworkTextColumnUiModel(
            sortedBy = SortedByUiModel.None,
            filter = TextFilterStateUiModel.EMPTY,
        )
    }
}

fun previewNetworkTextColumnUiModel() = NetworkTextColumnUiModel(
    sortedBy = SortedByUiModel.None,
    filter = previewTextFilterState(),
)
