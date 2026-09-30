package io.github.openflocon.flocondesktop.app.ui.settings

import io.github.openflocon.domain.models.settings.ThemeSetting

sealed interface SettingsAction {

    data class FontSizeMultiplierChange(val value: Float) : SettingsAction

    data class ThemeChange(val value: ThemeSetting) : SettingsAction
}
