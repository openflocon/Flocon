package io.github.openflocon.flocondesktop.features.network

import androidx.navigation3.runtime.EntryProviderBuilder
import io.github.openflocon.domain.settings.repository.SettingsRepository
import io.github.openflocon.flocondesktop.features.network.detail.view.NetworkDetailScreen
import io.github.openflocon.navigation.FloconRoute
import io.github.openflocon.navigation.scene.PanelSceneStrategy
import kotlinx.serialization.Serializable
import org.koin.mp.KoinPlatform

internal sealed interface NetworkRoutes : FloconRoute {

    @Serializable
    data class Panel(val requestId: String) : FloconRoute

}

fun EntryProviderBuilder<FloconRoute>.networkRoutes() {
    entry<NetworkRoutes.Panel>(
        metadata = PanelSceneStrategy.panel(
            pinnable = true,
            closable = true,
            onPin = {
                val repository = KoinPlatform.getKoin().get<SettingsRepository>()

                repository.networkSettings = repository.networkSettings.copy(pinnedDetails = true)
            }
        )
    ) {
        NetworkDetailScreen(requestId = it.requestId)
    }
}
