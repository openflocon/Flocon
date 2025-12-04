package io.github.openflocon.flocondesktop.features.crashreporter

import androidx.navigation3.runtime.EntryProviderScope
import io.github.openflocon.flocondesktop.app.MenuSceneStrategy
import io.github.openflocon.flocondesktop.features.table.view.TableScreen
import io.github.openflocon.navigation.FloconRoute
import kotlinx.serialization.Serializable

sealed interface CrashReporterRoutes : FloconRoute {

    @Serializable
    data object Main : CrashReporterRoutes
}

fun EntryProviderScope<FloconRoute>.crashReporterRoutes() {
    entry<CrashReporterRoutes.Main>(
        metadata = MenuSceneStrategy.menu()
    ) {
        CrashReporterScreen()
    }
}
