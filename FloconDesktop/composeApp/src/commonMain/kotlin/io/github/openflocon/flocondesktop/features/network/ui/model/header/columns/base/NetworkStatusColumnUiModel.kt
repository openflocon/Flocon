package io.github.openflocon.flocondesktop.features.network.ui.model.header.columns.base

import androidx.compose.runtime.Immutable
import io.github.openflocon.flocondesktop.features.network.ui.model.SortedByUiModel
import io.github.openflocon.flocondesktop.features.network.ui.model.header.columns.base.filter.StatusFilterState

@Immutable
data class NetworkStatusColumnUiModel(
    override val sortedBy: SortedByUiModel,
    override val filter: StatusFilterState,
) : NetworkColumnUiModel {
    companion object {
        val EMPTY = NetworkStatusColumnUiModel(
            sortedBy = SortedByUiModel.None,
            filter = StatusFilterState(isEnabled = false)
        )
    }
}

fun previewNetworkStatusColumnUiModel() = NetworkStatusColumnUiModel(
    sortedBy = SortedByUiModel.None,
    filter = StatusFilterState(isEnabled = false),
)
