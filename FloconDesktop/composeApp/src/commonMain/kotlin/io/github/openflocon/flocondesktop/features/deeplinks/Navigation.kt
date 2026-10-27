package io.github.openflocon.flocondesktop.features.deeplinks

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.metadata
import io.github.openflocon.flocondesktop.app.MenuSceneStrategy
import io.github.openflocon.flocondesktop.app.keepMenu
import io.github.openflocon.flocondesktop.app.menu
import io.github.openflocon.flocondesktop.features.deeplinks.view.DeeplinkPanel
import io.github.openflocon.flocondesktop.features.deeplinks.view.DeeplinkScreen
import io.github.openflocon.navigation.FloconRoute
import io.github.openflocon.navigation.PanelRoute
import io.github.openflocon.navigation.scene.PanelSceneStrategy.Companion.panel
import kotlinx.serialization.Serializable

sealed interface DeeplinkRoutes : FloconRoute {

    @Serializable
    data object Main : DeeplinkRoutes

    @Serializable
    data class Panel(
        val id: Long
    ) : DeeplinkRoutes,
        PanelRoute
}

fun EntryProviderScope<FloconRoute>.deeplinkRoutes() {
    entry<DeeplinkRoutes.Main>(
        metadata = metadata {
            menu()
        }
    ) {
        DeeplinkScreen()
    }
    entry<DeeplinkRoutes.Panel>(
        metadata = metadata {
            panel()
            keepMenu()
        }
    ) {
        DeeplinkPanel(
            id = it.id
        )
    }
}
