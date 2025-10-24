package io.github.openflocon.flocondesktop.features.network.body.model

import androidx.compose.runtime.Immutable
import io.github.openflocon.flocondesktop.features.network.model.NetworkBodyDetailUi
import java.util.UUID

@Immutable
data class ContentUiState(
    val selectedRequestId: String?,
    val detailJsons: Set<NetworkBodyDetailUi>,
    val mocksDisplayed: MockDisplayed?,
    val websocketMocksDisplayed: Boolean,
    val badNetworkQualityDisplayed: Boolean
)

@Immutable
data class MockDisplayed(
    val fromNetworkCallId: String?,
    val windowInstanceId: String = UUID.randomUUID().toString(),
)

fun previewContentUiState() = ContentUiState(
    selectedRequestId = null,
    detailJsons = emptySet(),
    mocksDisplayed = null,
    badNetworkQualityDisplayed = false,
    websocketMocksDisplayed = false
)
