package io.github.openflocon.flocondesktop.features.analytics

import androidx.navigation3.runtime.EntryProviderBuilder
import io.github.openflocon.flocondesktop.features.analytics.view.AnalyticsScreen
import io.github.openflocon.navigation.FloconRoute
import io.github.openflocon.navigation.scene.MenuScene
import kotlinx.serialization.Serializable

@Serializable
data object AnalyticsRoute : FloconRoute

fun EntryProviderBuilder<FloconRoute>.analyticsNavigation() {
    entry<AnalyticsRoute>(
        metadata = mapOf(MenuScene.MENU_KEY to true)
    ) {
        AnalyticsScreen()
    }
}
