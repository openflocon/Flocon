package io.github.openflocon.flocondesktop.features.accessibility

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.metadata
import io.github.openflocon.flocondesktop.app.keepMenu
import io.github.openflocon.flocondesktop.app.menu
import io.github.openflocon.flocondesktop.features.accessibility.view.AccessibilityDetailView
import io.github.openflocon.flocondesktop.features.accessibility.view.AccessibilityScreen
import io.github.openflocon.navigation.FloconRoute
import io.github.openflocon.navigation.PanelRoute
import io.github.openflocon.navigation.scene.PanelSceneStrategy.Companion.panel
import kotlinx.serialization.Serializable

sealed interface AccessibilityRoutes : FloconRoute {

    @Serializable
    data object Main : AccessibilityRoutes

    @Serializable
    data class Detail(
        val id: String,
    ) : AccessibilityRoutes,
        PanelRoute
}

fun EntryProviderScope<FloconRoute>.accessibilityRoutes() {
    entry<AccessibilityRoutes.Main>(
        metadata = metadata {
            menu()
        }
    ) {
        AccessibilityScreen()
    }
    entry<AccessibilityRoutes.Detail>(
        metadata = metadata {
            panel()
            keepMenu()
        }
    ) {
        AccessibilityDetailView(
            id = it.id,
        )
    }
}
