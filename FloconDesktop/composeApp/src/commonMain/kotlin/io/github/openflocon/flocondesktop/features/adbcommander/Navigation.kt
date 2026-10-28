package io.github.openflocon.flocondesktop.features.adbcommander

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.metadata
import io.github.openflocon.flocondesktop.app.MenuSceneStrategy
import io.github.openflocon.flocondesktop.app.keepMenu
import io.github.openflocon.flocondesktop.app.menu
import io.github.openflocon.flocondesktop.features.adbcommander.view.AdbCommandPanel
import io.github.openflocon.flocondesktop.features.adbcommander.view.AdbCommanderScreen
import io.github.openflocon.navigation.FloconRoute
import io.github.openflocon.navigation.PanelPinGroups
import io.github.openflocon.navigation.PanelRoute
import io.github.openflocon.navigation.scene.PanelSceneStrategy.Companion.panel
import kotlinx.serialization.Serializable

sealed interface AdbCommanderRoutes : FloconRoute {

    @Serializable
    data object Main : AdbCommanderRoutes

    @Serializable
    data class Panel(
        val id: Long,
        val fromTimeline: Boolean = false,
    ) : AdbCommanderRoutes,
        PanelRoute {

        // opened from the timeline: its pin state is the one of the timeline
        override val pinGroup: String?
            get() = if (fromTimeline) PanelPinGroups.Timeline else null
    }
}

fun EntryProviderScope<FloconRoute>.adbCommanderRoutes() {
    entry<AdbCommanderRoutes.Main>(
        metadata = metadata {
            menu()
        }
    ) {
        AdbCommanderScreen()
    }
    entry<AdbCommanderRoutes.Panel>(
        metadata = metadata {
            panel()
            keepMenu()
        }
    ) {
        AdbCommandPanel(
            id = it.id
        )
    }
}
