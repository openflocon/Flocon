package io.github.openflocon.flocondesktop.features.files

import androidx.navigation3.runtime.EntryProviderScope
import io.github.openflocon.flocondesktop.app.MenuSceneStrategy
import io.github.openflocon.flocondesktop.features.files.view.FilesScreen
import io.github.openflocon.navigation.FloconRoute
import kotlinx.serialization.Serializable

sealed interface FilesRoutes : FloconRoute {

    @Serializable
    data object Main : FilesRoutes

}

fun EntryProviderScope<FloconRoute>.filesRoutes() {
    entry<FilesRoutes.Main>(
        metadata = MenuSceneStrategy.menu()
    ) {
        FilesScreen()
    }
}
