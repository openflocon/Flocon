package io.github.openflocon.flocondesktop.features.network.ui.model.header.columns.base

import androidx.compose.runtime.Immutable
import io.github.openflocon.flocondesktop.features.network.ui.model.SortedByUiModel

@Immutable
data class NetworkMethodColumnUiModel(
    override val sortedBy: SortedByUiModel,
    override val filter: MethodFilterState,
) : NetworkColumnUiModel
