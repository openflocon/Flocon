package io.github.openflocon.flocondesktop.features.accessibility

import androidx.navigation3.runtime.EntryProviderScope
import io.github.openflocon.flocondesktop.app.MenuSceneStrategy
import io.github.openflocon.flocondesktop.features.accessibility.view.AccessibilityDetailView
import io.github.openflocon.flocondesktop.features.accessibility.view.AccessibilityScreen
import io.github.openflocon.navigation.FloconRoute
import io.github.openflocon.navigation.PanelRoute
import io.github.openflocon.navigation.scene.PanelSceneStrategy
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
        metadata = MenuSceneStrategy.menu()
    ) {
        AccessibilityScreen()
    }
    entry<AccessibilityRoutes.Detail>(
        metadata = PanelSceneStrategy.panel(
            pinnable = false,
            closable = true
        )
    ) {
        AccessibilityDetailView(
            id = it.id,
        )
    }
}
