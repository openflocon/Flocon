package io.github.openflocon.flocondesktop.features.images

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.metadata
import io.github.openflocon.flocondesktop.app.MenuSceneStrategy
import io.github.openflocon.flocondesktop.app.menu
import io.github.openflocon.flocondesktop.features.images.view.ImagesScreen
import io.github.openflocon.navigation.FloconRoute
import kotlinx.serialization.Serializable

sealed interface ImageRoutes : FloconRoute {

    @Serializable
    data object Main : ImageRoutes
}

fun EntryProviderScope<FloconRoute>.imageRoutes() {
    entry<ImageRoutes.Main>(
        metadata = metadata {
            menu()
        }
    ) {
        ImagesScreen()
    }
}
