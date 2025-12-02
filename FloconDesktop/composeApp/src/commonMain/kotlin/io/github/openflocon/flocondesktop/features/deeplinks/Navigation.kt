package io.github.openflocon.flocondesktop.features.deeplinks

import androidx.navigation3.runtime.EntryProviderScope
import io.github.openflocon.flocondesktop.app.MenuSceneStrategy
import io.github.openflocon.flocondesktop.features.deeplinks.view.DeeplinkScreen
import io.github.openflocon.navigation.FloconRoute
import kotlinx.serialization.Serializable

sealed interface DeeplinkRoutes : FloconRoute {

    @Serializable
    data object Main : DeeplinkRoutes
}

fun EntryProviderScope<FloconRoute>.deeplinkRoutes() {
    entry<DeeplinkRoutes.Main>(
        metadata = MenuSceneStrategy.menu()
    ) {
        DeeplinkScreen()
    }
}
