package io.github.openflocon.flocondesktop.features.network.ui

import androidx.compose.runtime.Immutable
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkBodyDetailUi

@Immutable
data class ContentUiState(
    val selectedRequestId: String?,
    val detailJsons: Set<NetworkBodyDetailUi>,
)

fun previewContentUiState() = ContentUiState(
    selectedRequestId = null,
    detailJsons = emptySet(),
)
