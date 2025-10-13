package io.github.openflocon.flocondesktop.features.network

import androidx.navigation3.runtime.EntryProviderBuilder
import io.github.openflocon.flocondesktop.features.network.list.view.NetworkScreen
import io.github.openflocon.navigation.FloconRoute
import io.github.openflocon.navigation.scene.MenuScene
import kotlinx.serialization.Serializable

@Serializable
data object NetworkRoute : FloconRoute

fun EntryProviderBuilder<FloconRoute>.networkNavigation() {
    entry<NetworkRoute>(
        metadata = mapOf(MenuScene.MENU_KEY to true)
    ) {
        NetworkScreen()
    }
}
