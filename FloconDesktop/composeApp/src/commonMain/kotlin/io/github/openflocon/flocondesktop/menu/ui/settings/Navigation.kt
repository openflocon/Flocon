package io.github.openflocon.flocondesktop.menu.ui.settings

import androidx.navigation3.runtime.EntryProviderBuilder
import io.github.openflocon.navigation.FloconRoute

data object SettingsRoute : FloconRoute

fun EntryProviderBuilder<FloconRoute>.settingsRoutes() {
    entry<SettingsRoute> {
        SettingsScreen()
    }
}
