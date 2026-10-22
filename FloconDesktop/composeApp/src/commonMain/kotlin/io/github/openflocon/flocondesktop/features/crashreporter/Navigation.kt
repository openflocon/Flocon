package io.github.openflocon.flocondesktop.features.crashreporter

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.metadata
import io.github.openflocon.flocondesktop.app.MenuSceneStrategy
import io.github.openflocon.flocondesktop.app.keepMenu
import io.github.openflocon.flocondesktop.app.menu
import io.github.openflocon.flocondesktop.features.crashreporter.view.CrashReportPanel
import io.github.openflocon.navigation.FloconRoute
import io.github.openflocon.navigation.PanelRoute
import io.github.openflocon.navigation.scene.PanelSceneStrategy.Companion.panel
import kotlinx.serialization.Serializable

sealed interface CrashReporterRoutes : FloconRoute {

    @Serializable
    data object Main : CrashReporterRoutes

    @Serializable
    data class Panel(
        val crashId: String
    ) : CrashReporterRoutes,
        PanelRoute
}

fun EntryProviderScope<FloconRoute>.crashReporterRoutes() {
    entry<CrashReporterRoutes.Main>(
        metadata = metadata {
            menu()
        }
    ) {
        CrashReporterScreen()
    }
    entry<CrashReporterRoutes.Panel>(
        metadata = metadata {
            panel()
            keepMenu()
        }
    ) {
        CrashReportPanel(crashId = it.crashId)
    }
}
