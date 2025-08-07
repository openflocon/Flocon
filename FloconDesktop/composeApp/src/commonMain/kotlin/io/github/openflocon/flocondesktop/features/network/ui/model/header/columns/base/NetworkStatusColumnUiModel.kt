package io.github.openflocon.flocondesktop.features.network.ui.model.header.columns.base

import androidx.compose.runtime.Immutable
import io.github.openflocon.flocondesktop.features.network.ui.model.SortedByUiModel
import io.github.openflocon.flocondesktop.features.network.ui.model.header.columns.base.filter.TextFilterStateUiModel
import io.github.openflocon.flocondesktop.features.network.ui.model.header.columns.base.filter.previewTextFilterState

@Immutable
data class NetworkStatusColumnUiModel(
    override val sortedBy: SortedByUiModel,
    override val filter: TextFilterStateUiModel, // TODO maybe later a specific filter
) : NetworkColumnUiModel {
    companion object {
        val EMPTY = NetworkStatusColumnUiModel(
            sortedBy = SortedByUiModel.None,
            filter = TextFilterStateUiModel.EMPTY,
        )
    }
}

fun previewNetworkStatusColumnUiModel() = NetworkStatusColumnUiModel(
    sortedBy = SortedByUiModel.None,
    filter = previewTextFilterState(),
)
