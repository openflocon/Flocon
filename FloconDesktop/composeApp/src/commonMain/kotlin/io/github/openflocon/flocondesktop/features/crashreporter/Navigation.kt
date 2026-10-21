package io.github.openflocon.flocondesktop.features.crashreporter

import androidx.navigation3.runtime.EntryProviderScope
import io.github.openflocon.flocondesktop.app.MenuSceneStrategy
import io.github.openflocon.flocondesktop.features.crashreporter.view.CrashReportPanel
import io.github.openflocon.navigation.FloconRoute
import io.github.openflocon.navigation.PanelRoute
import io.github.openflocon.navigation.scene.PanelSceneStrategy
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
        metadata = MenuSceneStrategy.menu()
    ) {
        CrashReporterScreen()
    }
    entry<CrashReporterRoutes.Panel>(
        metadata = PanelSceneStrategy.panel(
            pinnable = false,
            closable = true
        )
    ) {
        CrashReportPanel(crashId = it.crashId)
    }
}
