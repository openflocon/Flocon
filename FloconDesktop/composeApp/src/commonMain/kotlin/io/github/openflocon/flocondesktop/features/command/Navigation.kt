package io.github.openflocon.flocondesktop.features.command

import androidx.navigation3.runtime.EntryProviderScope
import io.github.openflocon.flocondesktop.app.MenuSceneStrategy
import io.github.openflocon.navigation.FloconRoute
import kotlinx.serialization.Serializable

sealed interface AdbCommandRoutes : FloconRoute {

    @Serializable
    data object Main : AdbCommandRoutes
}

fun EntryProviderScope<FloconRoute>.adbCommandRoutes() {
    entry<AdbCommandRoutes.Main>(
        metadata = MenuSceneStrategy.menu()
    ) {
        AdbCommandScreen()
    }
}
