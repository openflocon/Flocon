package io.github.openflocon.flocondesktop.features.network.ui.model.header.columns.base

import androidx.compose.runtime.Immutable
import io.github.openflocon.flocondesktop.features.network.ui.model.SortedByUiModel
import io.github.openflocon.flocondesktop.features.network.ui.model.header.columns.base.filter.TextFilterState

@Immutable
data class NetworkTextColumnUiModel(
    override val sortedBy: SortedByUiModel,
    override val filter: TextFilterState,
) : NetworkColumnUiModel

fun previewNetworkTextColumnUiModel() = NetworkTextColumnUiModel(
    sortedBy = SortedByUiModel.None,
    filter = TextFilterState(emptyList(), isEnabled = false)
)
