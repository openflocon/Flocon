package io.github.openflocon.flocondesktop.features.webview

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.metadata
import io.github.openflocon.flocondesktop.app.keepMenu
import io.github.openflocon.flocondesktop.app.menu
import io.github.openflocon.navigation.FloconRoute
import io.github.openflocon.navigation.PanelPinGroups
import io.github.openflocon.navigation.PanelRoute
import io.github.openflocon.navigation.scene.PanelSceneStrategy.Companion.panel
import kotlinx.serialization.Serializable

sealed interface WebViewRoutes : FloconRoute {

    @Serializable
    data object Main : WebViewRoutes

    // the detail of an event (id: the id of the event), opened from the timeline, so pinned with the other panels of the timeline
    @Serializable
    data class Detail(val id: String) :
        WebViewRoutes,
        PanelRoute {

        override val pinGroup: String? get() = PanelPinGroups.Timeline
    }
}

fun EntryProviderScope<FloconRoute>.webViewRoutes() {
    entry<WebViewRoutes.Main>(
        metadata = metadata {
            menu()
        }
    ) {
        WebViewScreen()
    }
    entry<WebViewRoutes.Detail>(
        metadata = metadata {
            panel()
            keepMenu()
        }
    ) {
        WebViewEventDetailScreen(id = it.id)
    }
}
