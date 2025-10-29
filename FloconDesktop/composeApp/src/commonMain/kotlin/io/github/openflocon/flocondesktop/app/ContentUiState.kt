package io.github.openflocon.flocondesktop.app

import androidx.compose.runtime.Immutable
import io.github.openflocon.flocondesktop.app.ui.model.SubScreen

@Immutable
data class ContentUiState(
    val current: SubScreen
)

fun previewContentUiState() = ContentUiState(
    current = SubScreen.Network
)
