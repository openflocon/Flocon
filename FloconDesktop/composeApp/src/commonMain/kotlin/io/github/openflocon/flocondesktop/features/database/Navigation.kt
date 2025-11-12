package io.github.openflocon.flocondesktop.features.database

import androidx.navigation3.runtime.EntryProviderScope
import io.github.openflocon.flocondesktop.app.MenuSceneStrategy
import io.github.openflocon.flocondesktop.features.database.view.DatabaseScreen
import io.github.openflocon.navigation.FloconRoute
import kotlinx.serialization.Serializable

sealed interface DatabaseRoutes : FloconRoute {

    @Serializable
    data object Main : DatabaseRoutes

}

fun EntryProviderScope<FloconRoute>.databaseRoutes() {
    entry<DatabaseRoutes.Main>(
        metadata = MenuSceneStrategy.menu()
    ) {
        DatabaseScreen()
    }
}
