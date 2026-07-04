package io.github.openflocon.flocondesktop.app.ui.settings

import androidx.compose.runtime.Immutable
import io.github.openflocon.domain.models.settings.ThemeSetting

@Immutable
data class SettingsUiState(
    val fontSizeMultiplier: Float,
    val theme: ThemeSetting
)

fun previewSettingsUiState() = SettingsUiState(
    fontSizeMultiplier = 1f,
    theme = ThemeSetting.DEFAULT
)
