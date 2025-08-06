package io.github.openflocon.flocondesktop.features.network.ui.model.header.columns.base

import androidx.compose.runtime.Immutable
import io.github.openflocon.flocondesktop.features.network.ui.model.SortedByUiModel

@Immutable
data class NetworkStatusColumnUiModel(
    override val sortedBy: SortedByUiModel,
    override val filter: StatusFilterState,
) : NetworkColumnUiModel
