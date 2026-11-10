package io.github.openflocon.flocondesktop.features.table

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.metadata
import io.github.openflocon.flocondesktop.app.MenuSceneStrategy
import io.github.openflocon.flocondesktop.app.keepMenu
import io.github.openflocon.flocondesktop.app.menu
import io.github.openflocon.flocondesktop.features.table.view.TableDetail
import io.github.openflocon.flocondesktop.features.table.view.TableScreen
import io.github.openflocon.navigation.FloconRoute
import io.github.openflocon.navigation.PanelPinGroups
import io.github.openflocon.navigation.PanelRoute
import io.github.openflocon.navigation.scene.PanelSceneStrategy.Companion.panel
import kotlinx.serialization.Serializable

sealed interface TableRoutes : FloconRoute {

    @Serializable
    data object Main : TableRoutes

    @Serializable
    data class Detail(
        val id: String,
        val fromTimeline: Boolean = false,
    ) : TableRoutes,
        PanelRoute {

        // opened from the timeline: its pin state is the one of the timeline
        override val pinGroup: String?
            get() = if (fromTimeline) PanelPinGroups.Timeline else null
    }
}

fun EntryProviderScope<FloconRoute>.tableRoutes() {
    entry<TableRoutes.Main>(
        metadata = metadata {
            menu()
        }
    ) {
        TableScreen()
    }
    entry<TableRoutes.Detail>(
        metadata = metadata {
            panel()
            keepMenu()
        }
    ) {
        TableDetail(
            id = it.id
        )
    }
}
