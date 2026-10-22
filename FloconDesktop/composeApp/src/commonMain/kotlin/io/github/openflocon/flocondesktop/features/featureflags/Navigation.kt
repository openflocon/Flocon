package io.github.openflocon.flocondesktop.features.featureflags

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.metadata
import io.github.openflocon.flocondesktop.app.MenuSceneStrategy
import io.github.openflocon.flocondesktop.app.keepMenu
import io.github.openflocon.flocondesktop.app.menu
import io.github.openflocon.flocondesktop.features.featureflags.view.FeatureFlagDetailView
import io.github.openflocon.flocondesktop.features.featureflags.view.FeatureFlagsScreen
import io.github.openflocon.navigation.FloconRoute
import io.github.openflocon.navigation.PanelRoute
import io.github.openflocon.navigation.scene.PanelSceneStrategy.Companion.panel
import kotlinx.serialization.Serializable

sealed interface FeatureFlagsRoutes : FloconRoute {

    @Serializable
    data object Main : FeatureFlagsRoutes

    @Serializable
    data class Detail(
        val provider: String,
        val name: String,
    ) : FeatureFlagsRoutes,
        PanelRoute
}

fun EntryProviderScope<FloconRoute>.featureFlagsRoutes() {
    entry<FeatureFlagsRoutes.Main>(
        metadata = metadata {
            menu()
        }
    ) {
        FeatureFlagsScreen()
    }
    entry<FeatureFlagsRoutes.Detail>(
        metadata = metadata {
            panel()
            keepMenu()
        }
    ) {
        FeatureFlagDetailView(
            provider = it.provider,
            name = it.name,
        )
    }
}
