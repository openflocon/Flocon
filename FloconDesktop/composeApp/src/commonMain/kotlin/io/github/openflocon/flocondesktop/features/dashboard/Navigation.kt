package io.github.openflocon.flocondesktop.features.dashboard

import androidx.navigation3.runtime.EntryProviderScope
import io.github.openflocon.flocondesktop.app.MenuSceneStrategy
import io.github.openflocon.flocondesktop.features.dashboard.view.DashboardScreen
import io.github.openflocon.navigation.FloconRoute
import kotlinx.serialization.Serializable

sealed interface DashboardRoutes : FloconRoute {

    @Serializable
    data object Main : DashboardRoutes

}

fun EntryProviderScope<FloconRoute>.dashboardRoutes() {
    entry<DashboardRoutes.Main>(
        metadata = MenuSceneStrategy.menu()
    ) {
        DashboardScreen()
    }
}
