package io.github.openflocon.flocondesktop.features.network.ui.model.header

import androidx.compose.runtime.Immutable
import io.github.openflocon.flocondesktop.features.network.ui.model.header.columns.base.NetworkMethodColumnUiModel
import io.github.openflocon.flocondesktop.features.network.ui.model.header.columns.base.NetworkStatusColumnUiModel
import io.github.openflocon.flocondesktop.features.network.ui.model.header.columns.base.NetworkTextColumnUiModel
import io.github.openflocon.flocondesktop.features.network.ui.model.header.columns.base.previewNetworkMethodColumnUiModel
import io.github.openflocon.flocondesktop.features.network.ui.model.header.columns.base.previewNetworkStatusColumnUiModel
import io.github.openflocon.flocondesktop.features.network.ui.model.header.columns.base.previewNetworkTextColumnUiModel

@Immutable
data class NetworkHeaderUiState(
    val requestTime: NetworkTextColumnUiModel,
    val method: NetworkMethodColumnUiModel,
    val domain: NetworkTextColumnUiModel,
    val query: NetworkTextColumnUiModel,
    val status: NetworkStatusColumnUiModel,
    val time: NetworkTextColumnUiModel,
)

fun previewNetworkHeaderUiState() = NetworkHeaderUiState(
    requestTime = previewNetworkTextColumnUiModel(),
    method = previewNetworkMethodColumnUiModel(),
    domain = previewNetworkTextColumnUiModel(),
    query = previewNetworkTextColumnUiModel(),
    status = previewNetworkStatusColumnUiModel(),
    time = previewNetworkTextColumnUiModel(),
)
