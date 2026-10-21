package io.github.openflocon.flocondesktop.app.ui.settings

import io.github.openflocon.domain.models.settings.AppScaleSetting
import io.github.openflocon.domain.models.settings.ThemeSetting

sealed interface SettingsAction {

    data class AppScaleChange(val value: AppScaleSetting) : SettingsAction

    data class ThemeChange(val value: ThemeSetting) : SettingsAction
}
