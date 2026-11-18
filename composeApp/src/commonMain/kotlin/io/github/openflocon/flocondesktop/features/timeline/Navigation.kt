package io.github.openflocon.flocondesktop.features.timeline

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.metadata
import io.github.openflocon.flocondesktop.app.keepMenu
import io.github.openflocon.flocondesktop.app.menu
import io.github.openflocon.navigation.FloconRoute
import io.github.openflocon.navigation.PanelPinGroups
import io.github.openflocon.navigation.PanelRoute
import io.github.openflocon.navigation.scene.PanelSceneStrategy.Companion.panel
import kotlinx.serialization.Serializable

sealed interface TimelineRoutes : FloconRoute {

    @Serializable
    data object Main : TimelineRoutes

    // the filters of the timeline, pinned with the other panels of the timeline
    @Serializable
    data object Filters :
        TimelineRoutes,
        PanelRoute {

        override val pinGroup: String? get() = PanelPinGroups.Timeline
    }

    // the detail of a log (id: the id of the log in the table of the logs), pinned with the other panels of the timeline
    @Serializable
    data class LogDetail(val id: Long) :
        TimelineRoutes,
        PanelRoute {

        override val pinGroup: String? get() = PanelPinGroups.Timeline
    }
}

fun EntryProviderScope<FloconRoute>.timelineRoutes() {
    entry<TimelineRoutes.Main>(
        metadata = metadata {
            menu()
        }
    ) {
        TimelineScreen()
    }
    entry<TimelineRoutes.Filters>(
        metadata = metadata {
            panel()
            keepMenu()
        }
    ) {
        TimelineFilterPanelScreen()
    }
    entry<TimelineRoutes.LogDetail>(
        metadata = metadata {
            panel()
            keepMenu()
        }
    ) {
        TimelineLogDetailScreen(id = it.id)
    }
}
