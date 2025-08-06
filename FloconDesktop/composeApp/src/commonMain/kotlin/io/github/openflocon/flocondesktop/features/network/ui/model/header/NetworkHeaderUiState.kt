package io.github.openflocon.flocondesktop.features.network.ui.model.header

import androidx.compose.runtime.Immutable
import io.github.openflocon.flocondesktop.features.network.ui.model.header.columns.NetworkTextColumnUiModel
import io.github.openflocon.flocondesktop.features.network.ui.view.header.NetworkMethodColumnUiModel
import io.github.openflocon.flocondesktop.features.network.ui.view.header.StatusDomainColumnUiModel

@Immutable
data class NetworkHeaderUiState(
    val requestTime: NetworkTextColumnUiModel,
    val method: NetworkMethodColumnUiModel,
    val domain: NetworkTextColumnUiModel,
    val query: NetworkTextColumnUiModel,
    val status: StatusDomainColumnUiModel,
    val time: NetworkTextColumnUiModel,
)
