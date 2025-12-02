package io.github.openflocon.flocondesktop.app.ui.settings

import androidx.navigation3.runtime.EntryProviderScope
import io.github.openflocon.flocondesktop.app.MenuSceneStrategy
import io.github.openflocon.navigation.FloconRoute
import kotlinx.serialization.Serializable

sealed interface SettingsRoutes : FloconRoute {

    @Serializable
    data object Main : SettingsRoutes
}

fun EntryProviderScope<FloconRoute>.settingsRoutes() {
    entry<SettingsRoutes.Main>(
        metadata = MenuSceneStrategy.menu(),
    ) {
        SettingsScreen()
    }
}
