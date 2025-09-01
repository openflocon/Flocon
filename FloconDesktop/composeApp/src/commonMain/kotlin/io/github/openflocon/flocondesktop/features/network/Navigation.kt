package io.github.openflocon.flocondesktop.features.network

import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.entry
import io.github.openflocon.flocondesktop.features.network.list.view.NetworkScreen
import io.github.openflocon.navigation.FloconRoute
import kotlinx.serialization.Serializable

@Serializable
data object NetworkRoute : FloconRoute

fun EntryProviderBuilder<FloconRoute>.networkNavigation() {
    entry<NetworkRoute> {
        NetworkScreen()
    }
}
