package io.github.openflocon.flocondesktop.main.ui.settings

import androidx.compose.runtime.Immutable

@Immutable
data class SettingsUiState(
    val fontSizeMultiplier: Float
)

fun previewSettingsUiState() = SettingsUiState(
    fontSizeMultiplier = 1f
)
