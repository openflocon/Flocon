package io.github.openflocon.flocondesktop.menu.ui.settings

import androidx.navigation3.runtime.EntryProviderScope
import io.github.openflocon.navigation.FloconRoute
import kotlinx.serialization.Serializable

@Serializable
data object SettingsRoute : FloconRoute

fun EntryProviderScope<FloconRoute>.settingsRoutes() {
    entry<SettingsRoute> {
        SettingsScreen()
    }
}
