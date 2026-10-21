package io.github.openflocon.flocondesktop.features.table

import androidx.navigation3.runtime.EntryProviderScope
import io.github.openflocon.flocondesktop.app.MenuSceneStrategy
import io.github.openflocon.flocondesktop.features.table.view.TableDetailView
import io.github.openflocon.flocondesktop.features.table.view.TableScreen
import io.github.openflocon.navigation.FloconRoute
import io.github.openflocon.navigation.PanelRoute
import io.github.openflocon.navigation.scene.PanelSceneStrategy
import kotlinx.serialization.Serializable

sealed interface TableRoutes : FloconRoute {

    @Serializable
    data object Main : TableRoutes

    @Serializable
    data class Detail(
        val id: String
    ) : TableRoutes,
        PanelRoute
}

fun EntryProviderScope<FloconRoute>.tableRoutes() {
    entry<TableRoutes.Main>(
        metadata = MenuSceneStrategy.menu()
    ) {
        TableScreen()
    }
    entry<TableRoutes.Detail>(
        metadata = PanelSceneStrategy.panel(
            pinnable = false,
            closable = true
        )
    ) {
        TableDetailView(
            id = it.id
        )
    }
}
