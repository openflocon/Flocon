package io.github.openflocon.flocondesktop.app.ui.settings

import io.github.openflocon.domain.models.settings.AppScaleSetting
import io.github.openflocon.domain.models.settings.ThemeSetting

sealed interface SettingsAction {

    data class AppScaleChange(val value: AppScaleSetting) : SettingsAction

    data class ThemeChange(val value: ThemeSetting) : SettingsAction

    data class MascotEnabledChange(val enabled: Boolean) : SettingsAction

    // the database transactions show their queries until the user folds them
    data class TransactionsExpandedByDefaultChange(val expanded: Boolean) : SettingsAction

    // the screen displayed when the app starts
    data class StartScreenChange(val screenId: String) : SettingsAction

    // shows or hides a plugin in the left panel
    data class ToggleMenuItem(val itemId: String) : SettingsAction
}
