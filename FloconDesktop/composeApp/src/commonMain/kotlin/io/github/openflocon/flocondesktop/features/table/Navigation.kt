package io.github.openflocon.flocondesktop.features.table

import androidx.navigation3.runtime.EntryProviderScope
import io.github.openflocon.flocondesktop.app.MenuSceneStrategy
import io.github.openflocon.flocondesktop.features.table.view.TableScreen
import io.github.openflocon.navigation.FloconRoute
import kotlinx.serialization.Serializable

sealed interface TableRoutes : FloconRoute {

    @Serializable
    data object Main : TableRoutes
}

fun EntryProviderScope<FloconRoute>.tableRoutes() {
    entry<TableRoutes.Main>(
        metadata = MenuSceneStrategy.menu()
    ) {
        TableScreen()
    }
}
