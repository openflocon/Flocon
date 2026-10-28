package io.github.openflocon.flocondesktop.features.deeplinks

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.metadata
import io.github.openflocon.flocondesktop.app.MenuSceneStrategy
import io.github.openflocon.flocondesktop.app.keepMenu
import io.github.openflocon.flocondesktop.app.menu
import io.github.openflocon.flocondesktop.features.deeplinks.view.DeeplinkPanel
import io.github.openflocon.flocondesktop.features.deeplinks.view.DeeplinkScreen
import io.github.openflocon.navigation.FloconRoute
import io.github.openflocon.navigation.PanelPinGroups
import io.github.openflocon.navigation.PanelRoute
import io.github.openflocon.navigation.scene.PanelSceneStrategy.Companion.panel
import kotlinx.serialization.Serializable

sealed interface DeeplinkRoutes : FloconRoute {

    @Serializable
    data object Main : DeeplinkRoutes

    @Serializable
    data class Panel(
        val id: Long,
        val fromTimeline: Boolean = false,
    ) : DeeplinkRoutes,
        PanelRoute {

        // opened from the timeline: its pin state is the one of the timeline
        override val pinGroup: String?
            get() = if (fromTimeline) PanelPinGroups.Timeline else null
    }
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
