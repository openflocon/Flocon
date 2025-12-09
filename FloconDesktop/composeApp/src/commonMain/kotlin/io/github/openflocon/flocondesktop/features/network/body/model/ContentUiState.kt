package io.github.openflocon.flocondesktop.features.network.body.model

import androidx.compose.runtime.Immutable

@Immutable
data class ContentUiState(
    val selectedRequestId: String?,
    val websocketMocksDisplayed: Boolean,
    val badNetworkQualityDisplayed: Boolean,
    val selecting: Boolean,
    val multiSelectedIds: Set<String>
)

fun previewContentUiState() = ContentUiState(
    selectedRequestId = null,
    badNetworkQualityDisplayed = false,
    websocketMocksDisplayed = false,
    selecting = false,
    multiSelectedIds = emptySet()
)
