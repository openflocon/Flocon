package io.github.openflocon.flocondesktop.app.ui.settings

import androidx.navigation3.runtime.EntryProviderScope
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.menu_item_settings
import io.github.openflocon.navigation.FloconRoute
import io.github.openflocon.navigation.scene.BigDialogProperties
import io.github.openflocon.navigation.scene.BigDialogSceneStrategy
import kotlinx.serialization.Serializable

sealed interface SettingsRoutes : FloconRoute {

    @Serializable
    data object Main : SettingsRoutes
}

fun EntryProviderScope<FloconRoute>.settingsRoutes() {
    entry<SettingsRoutes.Main>(
        metadata = BigDialogSceneStrategy.bigDialog(BigDialogProperties(Res.string.menu_item_settings))
    ) {
        SettingsScreen()
    }
}
