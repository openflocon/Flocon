package io.github.openflocon.flocondesktop.features.network.ui

import androidx.compose.runtime.Immutable
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkJsonUi

@Immutable
data class ContentUiState(
    val selectedRequestId: String?,
    val detailJsons: Set<NetworkJsonUi>
)

fun previewContentUiState() = ContentUiState(
    selectedRequestId = null,
    detailJsons = emptySet()
)
