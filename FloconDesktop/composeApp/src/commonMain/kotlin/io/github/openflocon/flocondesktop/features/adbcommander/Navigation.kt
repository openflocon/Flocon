package io.github.openflocon.flocondesktop.features.adbcommander

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.metadata
import io.github.openflocon.flocondesktop.app.MenuSceneStrategy
import io.github.openflocon.flocondesktop.app.keepMenu
import io.github.openflocon.flocondesktop.app.menu
import io.github.openflocon.flocondesktop.features.adbcommander.view.AdbCommandPanel
import io.github.openflocon.flocondesktop.features.adbcommander.view.AdbCommanderScreen
import io.github.openflocon.navigation.FloconRoute
import io.github.openflocon.navigation.PanelRoute
import io.github.openflocon.navigation.scene.PanelSceneStrategy.Companion.panel
import kotlinx.serialization.Serializable

sealed interface AdbCommanderRoutes : FloconRoute {

    @Serializable
    data object Main : AdbCommanderRoutes

    @Serializable
    data class Panel(
        val id: Long
    ) : AdbCommanderRoutes,
        PanelRoute
}

fun EntryProviderScope<FloconRoute>.adbCommanderRoutes() {
    entry<AdbCommanderRoutes.Main>(
        metadata = metadata {
            menu()
        }
    ) {
        AdbCommanderScreen()
    }
    entry<AdbCommanderRoutes.Panel>(
        metadata = metadata {
            panel()
            keepMenu()
        }
    ) {
        AdbCommandPanel(
            id = it.id
        )
    }
}
