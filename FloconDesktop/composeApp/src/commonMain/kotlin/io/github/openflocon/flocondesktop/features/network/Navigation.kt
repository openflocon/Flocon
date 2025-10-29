package io.github.openflocon.flocondesktop.features.network

import androidx.navigation3.runtime.EntryProviderScope
import io.github.openflocon.domain.settings.repository.SettingsRepository
import io.github.openflocon.flocondesktop.app.MenuSceneStrategy
import io.github.openflocon.flocondesktop.app.ui.model.SubScreen
import io.github.openflocon.flocondesktop.features.network.detail.view.NetworkDetailScreen
import io.github.openflocon.flocondesktop.features.network.list.view.NetworkScreen
import io.github.openflocon.navigation.FloconRoute
import io.github.openflocon.navigation.PanelRoute
import io.github.openflocon.navigation.scene.PanelSceneStrategy
import kotlinx.serialization.Serializable
import org.koin.mp.KoinPlatform

internal sealed interface NetworkRoutes {

    @Serializable
    data object Main : PanelRoute

    @Serializable
    data class Panel(val requestId: String) : PanelRoute

}

fun EntryProviderScope<FloconRoute>.networkRoutes() {
    entry<NetworkRoutes.Main>(
        metadata = MenuSceneStrategy.menu(SubScreen.Network)
    ) {
        NetworkScreen()
    }
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
