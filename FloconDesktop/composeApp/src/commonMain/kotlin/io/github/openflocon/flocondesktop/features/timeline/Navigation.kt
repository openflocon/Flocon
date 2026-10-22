package io.github.openflocon.flocondesktop.features.timeline

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.metadata
import io.github.openflocon.flocondesktop.app.menu
import io.github.openflocon.navigation.FloconRoute
import kotlinx.serialization.Serializable

sealed interface TimelineRoutes : FloconRoute {

    @Serializable
    data object Main : TimelineRoutes
}

fun EntryProviderScope<FloconRoute>.timelineRoutes() {
    entry<TimelineRoutes.Main>(
        metadata = metadata {
            menu()
        }
    ) {
        TimelineScreen()
    }
}
