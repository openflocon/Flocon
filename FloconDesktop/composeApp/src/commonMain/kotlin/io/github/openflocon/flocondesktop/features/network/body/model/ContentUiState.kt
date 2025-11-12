package io.github.openflocon.flocondesktop.features.network.body.model

import androidx.compose.runtime.Immutable
import io.github.openflocon.flocondesktop.features.network.model.NetworkBodyDetailUi

@Immutable
data class ContentUiState(
    val selectedRequestId: String?,
    val detailJsons: Set<NetworkBodyDetailUi>,
    val websocketMocksDisplayed: Boolean,
    val badNetworkQualityDisplayed: Boolean
)

fun previewContentUiState() = ContentUiState(
    selectedRequestId = null,
    detailJsons = emptySet(),
    badNetworkQualityDisplayed = false,
    websocketMocksDisplayed = false
)
