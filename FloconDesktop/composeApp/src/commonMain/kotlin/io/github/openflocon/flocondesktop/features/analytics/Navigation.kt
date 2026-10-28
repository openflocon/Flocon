package io.github.openflocon.flocondesktop.features.analytics

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.metadata
import io.github.openflocon.flocondesktop.app.MenuSceneStrategy
import io.github.openflocon.flocondesktop.app.keepMenu
import io.github.openflocon.flocondesktop.app.menu
import io.github.openflocon.flocondesktop.features.analytics.view.AnalyticsDetail
import io.github.openflocon.flocondesktop.features.analytics.view.AnalyticsScreen
import io.github.openflocon.navigation.FloconRoute
import io.github.openflocon.navigation.PanelPinGroups
import io.github.openflocon.navigation.PanelRoute
import io.github.openflocon.navigation.scene.PanelSceneStrategy.Companion.panel
import kotlinx.serialization.Serializable

sealed interface AnalyticsRoutes : FloconRoute {

    @Serializable
    data object Main : AnalyticsRoutes

    @Serializable
    data class Detail(
        val id: String,
        val fromTimeline: Boolean = false,
    ) : AnalyticsRoutes,
        PanelRoute {

        // opened from the timeline: its pin state is the one of the timeline
        override val pinGroup: String?
            get() = if (fromTimeline) PanelPinGroups.Timeline else null
    }
}

fun EntryProviderScope<FloconRoute>.analyticsRoutes() {
    entry<AnalyticsRoutes.Main>(
        metadata = metadata {
            menu()
        }
    ) {
        AnalyticsScreen()
    }
    entry<AnalyticsRoutes.Detail>(
        metadata = metadata {
            panel()
            keepMenu()
        }
    ) {
        AnalyticsDetail(
            id = it.id
        )
    }
}
