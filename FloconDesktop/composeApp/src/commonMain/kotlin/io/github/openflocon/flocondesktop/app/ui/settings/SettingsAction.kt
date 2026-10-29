package io.github.openflocon.flocondesktop.app.ui.settings

import io.github.openflocon.domain.models.settings.AppScaleSetting
import io.github.openflocon.domain.models.settings.ThemeSetting

sealed interface SettingsAction {

    data class AppScaleChange(val value: AppScaleSetting) : SettingsAction

    data class ThemeChange(val value: ThemeSetting) : SettingsAction

    data class MascotEnabledChange(val enabled: Boolean) : SettingsAction

    // shows or hides a plugin in the left panel
    data class ToggleMenuItem(val itemId: String) : SettingsAction
}
