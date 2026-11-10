package io.github.openflocon.flocondesktop.features.sharedpreferences

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.metadata
import io.github.openflocon.flocondesktop.app.keepMenu
import io.github.openflocon.flocondesktop.app.menu
import io.github.openflocon.flocondesktop.features.sharedpreferences.view.SharedPreferenceEditPanel
import io.github.openflocon.flocondesktop.features.sharedpreferences.view.SharedPreferencesScreen
import io.github.openflocon.navigation.FloconRoute
import io.github.openflocon.navigation.PanelRoute
import io.github.openflocon.navigation.scene.PanelSceneStrategy.Companion.panel
import kotlinx.serialization.Serializable

sealed interface SharedPreferencesRoutes : FloconRoute {

    @Serializable
    data object Main : SharedPreferencesRoutes

    // the editor of a long / json value, in the right panel
    @Serializable
    data class Edit(val key: String) :
        SharedPreferencesRoutes,
        PanelRoute
}

fun EntryProviderScope<FloconRoute>.sharedPreferencesRoutes() {
    entry<SharedPreferencesRoutes.Main>(
        metadata = metadata {
            menu()
        }
    ) {
        SharedPreferencesScreen()
    }
    entry<SharedPreferencesRoutes.Edit>(
        metadata = metadata {
            panel()
            keepMenu()
        }
    ) {
        SharedPreferenceEditPanel(key = it.key)
    }
}
