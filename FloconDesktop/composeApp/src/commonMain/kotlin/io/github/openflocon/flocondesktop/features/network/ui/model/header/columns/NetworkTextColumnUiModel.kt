package io.github.openflocon.flocondesktop.features.network.ui.model.header.columns

import androidx.compose.runtime.Immutable
import io.github.openflocon.flocondesktop.features.network.ui.model.SortedByUiModel
import io.github.openflocon.flocondesktop.features.network.ui.model.header.columns.base.NetworkColumnUiModel
import io.github.openflocon.flocondesktop.features.network.ui.model.header.columns.base.TextFilterState

@Immutable
data class NetworkTextColumnUiModel(
    override val sortedBy: SortedByUiModel,
    override val filter: TextFilterState,
) : NetworkColumnUiModel
