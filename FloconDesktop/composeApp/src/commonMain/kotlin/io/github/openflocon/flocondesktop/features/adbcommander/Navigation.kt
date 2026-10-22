package io.github.openflocon.flocondesktop.features.adbcommander

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.metadata
import io.github.openflocon.flocondesktop.app.MenuSceneStrategy
import io.github.openflocon.flocondesktop.app.menu
import io.github.openflocon.flocondesktop.features.adbcommander.view.AdbCommanderScreen
import io.github.openflocon.navigation.FloconRoute
import kotlinx.serialization.Serializable

sealed interface AdbCommanderRoutes : FloconRoute {

    @Serializable
    data object Main : AdbCommanderRoutes
}

fun EntryProviderScope<FloconRoute>.adbCommanderRoutes() {
    entry<AdbCommanderRoutes.Main>(
        metadata = metadata {
            menu()
        }
    ) {
        AdbCommanderScreen()
    }
}
