package io.github.openflocon.flocondesktop.features.analytics

import androidx.navigation3.runtime.EntryProviderScope
import io.github.openflocon.flocondesktop.app.MenuSceneStrategy
import io.github.openflocon.flocondesktop.app.ui.model.SubScreen
import io.github.openflocon.flocondesktop.features.analytics.view.AnalyticsScreen
import io.github.openflocon.navigation.FloconRoute
import kotlinx.serialization.Serializable

sealed interface AnalyticsRoutes : FloconRoute {

    @Serializable
    data object Main : AnalyticsRoutes

}

fun EntryProviderScope<FloconRoute>.analyticsRoutes() {
    entry<AnalyticsRoutes.Main>(
        metadata = MenuSceneStrategy.menu(SubScreen.Analytics)
    ) {
        AnalyticsScreen()
    }
}
