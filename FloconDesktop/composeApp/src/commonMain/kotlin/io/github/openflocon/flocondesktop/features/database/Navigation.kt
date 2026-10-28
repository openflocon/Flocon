package io.github.openflocon.flocondesktop.features.database

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.metadata
import io.github.openflocon.flocondesktop.app.MenuSceneStrategy
import io.github.openflocon.flocondesktop.app.keepMenu
import io.github.openflocon.flocondesktop.app.menu
import io.github.openflocon.flocondesktop.features.database.view.DatabaseDetails
import io.github.openflocon.flocondesktop.features.database.view.DatabaseQueryLogPanel
import io.github.openflocon.flocondesktop.features.database.view.DatabaseScreen
import io.github.openflocon.flocondesktop.features.database.view.DetailResultItem
import io.github.openflocon.navigation.FloconRoute
import io.github.openflocon.navigation.PanelPinGroups
import io.github.openflocon.navigation.PanelRoute
import io.github.openflocon.navigation.scene.PanelSceneStrategy.Companion.panel
import kotlinx.serialization.Serializable

sealed interface DatabaseRoutes : FloconRoute {

    @Serializable
    data object Main : DatabaseRoutes

    @Serializable
    data class Detail(
        val result: DetailResultItem,
        val columns: List<String>
    ) : DatabaseRoutes,
        PanelRoute

    @Serializable
    data class QueryLogPanel(
        val id: Int,
        val fromTimeline: Boolean = false,
    ) : DatabaseRoutes,
        PanelRoute {

        // opened from the timeline: its pin state is the one of the timeline
        override val pinGroup: String?
            get() = if (fromTimeline) PanelPinGroups.Timeline else null
    }
}

fun EntryProviderScope<FloconRoute>.databaseRoutes() {
    entry<DatabaseRoutes.Main>(
        metadata = metadata {
            menu()
        }
    ) {
        DatabaseScreen()
    }
    entry<DatabaseRoutes.Detail>(
        metadata = metadata {
            panel()
            keepMenu()
        }
    ) {
        DatabaseDetails(
            state = it.result,
            columns = it.columns
        )
    }
    entry<DatabaseRoutes.QueryLogPanel>(
        metadata = metadata {
            panel()
            keepMenu()
        }
    ) {
        DatabaseQueryLogPanel(
            id = it.id
        )
    }
}
