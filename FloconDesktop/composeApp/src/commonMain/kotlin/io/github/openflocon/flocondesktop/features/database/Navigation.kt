package io.github.openflocon.flocondesktop.features.database

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.metadata
import io.github.openflocon.flocondesktop.app.MenuSceneStrategy
import io.github.openflocon.flocondesktop.app.keepMenu
import io.github.openflocon.flocondesktop.app.menu
import io.github.openflocon.flocondesktop.features.database.view.DatabaseDetails
import io.github.openflocon.flocondesktop.features.database.view.DatabaseScreen
import io.github.openflocon.flocondesktop.features.database.view.DetailResultItem
import io.github.openflocon.navigation.FloconRoute
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
}
