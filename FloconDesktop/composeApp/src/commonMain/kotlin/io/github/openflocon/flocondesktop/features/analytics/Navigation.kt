package io.github.openflocon.flocondesktop.features.analytics

import androidx.navigation3.runtime.EntryProviderScope
import io.github.openflocon.flocondesktop.app.MenuSceneStrategy
import io.github.openflocon.flocondesktop.features.analytics.view.AnalyticsDetailView
import io.github.openflocon.flocondesktop.features.analytics.view.AnalyticsScreen
import io.github.openflocon.navigation.FloconRoute
import io.github.openflocon.navigation.PanelRoute
import io.github.openflocon.navigation.scene.PanelSceneStrategy
import kotlinx.serialization.Serializable

sealed interface AnalyticsRoutes : FloconRoute {

    @Serializable
    data object Main : AnalyticsRoutes

    @Serializable
    data class Detail(
        val id: String
    ) : AnalyticsRoutes, PanelRoute

}

fun EntryProviderScope<FloconRoute>.analyticsRoutes() {
    entry<AnalyticsRoutes.Main>(
        metadata = MenuSceneStrategy.menu()
    ) {
        AnalyticsScreen()
    }
    entry<AnalyticsRoutes.Detail>(
        metadata = PanelSceneStrategy.panel(
            pinnable = false,
            closable = true
        )
    ) {
        AnalyticsDetailView(
            id = it.id
        )
    }
}
