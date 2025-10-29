package io.github.openflocon.flocondesktop.features.network

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.scene.DialogSceneStrategy
import io.github.openflocon.domain.settings.repository.SettingsRepository
import io.github.openflocon.flocondesktop.app.MenuSceneStrategy
import io.github.openflocon.flocondesktop.features.network.detail.view.NetworkDetailScreen
import io.github.openflocon.flocondesktop.features.network.list.view.NetworkScreen
import io.github.openflocon.flocondesktop.features.network.mock.list.view.NetworkMocksWindow
import io.github.openflocon.navigation.FloconRoute
import io.github.openflocon.navigation.MainFloconNavigationState
import io.github.openflocon.navigation.PanelRoute
import io.github.openflocon.navigation.scene.PanelSceneStrategy
import kotlinx.serialization.Serializable
import org.koin.mp.KoinPlatform

internal sealed interface NetworkRoutes : FloconRoute {

    @Serializable
    data object Main : NetworkRoutes

    @Serializable
    data object Mocks : NetworkRoutes

    @Serializable
    data class Panel(val requestId: String) : NetworkRoutes, PanelRoute

}

fun EntryProviderScope<FloconRoute>.networkRoutes(navigationState: MainFloconNavigationState) {
    entry<NetworkRoutes.Main>(
        metadata = MenuSceneStrategy.menu()
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
    entry<NetworkRoutes.Mocks>(
        metadata = DialogSceneStrategy.dialog()
    ) {
        NetworkMocksWindow(
            instanceId = "instance",
            fromNetworkCallId = null,
            onCloseRequest = { navigationState.remove(it) }
        )
    }
}
