@file:OptIn(ExperimentalUuidApi::class)

package io.github.openflocon.flocondesktop.features.network

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.scene.DialogSceneStrategy
import io.github.openflocon.domain.settings.repository.SettingsRepository
import io.github.openflocon.flocondesktop.app.MenuSceneStrategy
import io.github.openflocon.flocondesktop.features.network.body.NetworkBodyWindow
import io.github.openflocon.flocondesktop.features.network.body.model.NetworkBodyDetailUi
import io.github.openflocon.flocondesktop.features.network.detail.view.NetworkDetailScreen
import io.github.openflocon.flocondesktop.features.network.list.view.NetworkScreen
import io.github.openflocon.flocondesktop.features.network.mock.list.view.NetworkMocksWindow
import io.github.openflocon.flocondesktop.features.network.search.view.NetworkSearchScreen
import io.github.openflocon.navigation.FloconRoute
import io.github.openflocon.navigation.PanelRoute
import io.github.openflocon.navigation.WindowRoute
import io.github.openflocon.navigation.scene.PanelSceneStrategy
import io.github.openflocon.navigation.scene.WindowSceneStrategy
import kotlinx.serialization.Serializable
import org.koin.mp.KoinPlatform
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

internal sealed interface NetworkRoutes : FloconRoute {

    @Serializable
    data object Main : NetworkRoutes

    @Serializable
    data class Mocks(val id: String?) : NetworkRoutes

    @Serializable
    data class Panel(val requestId: String) :
        NetworkRoutes,
        PanelRoute

    @Serializable
    data class WindowDetail(
        val requestId: String,
        val windowKey: String,
    ) : NetworkRoutes

    @Serializable
    data object DeepSearch : NetworkRoutes, WindowRoute {
        override val singleTopKey = "deepsearch"
    }

    @Serializable
    data class JsonDetail(
        val json: String,
        val id: String = Uuid.random().toString()
    ) : NetworkRoutes
}

fun EntryProviderScope<FloconRoute>.networkRoutes() {
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

                repository.networkSettings =
                    repository.networkSettings.copy(pinnedDetails = true)
            }
        )
    ) {
        NetworkDetailScreen(requestId = it.requestId)
    }
    entry<NetworkRoutes.Mocks>(
        metadata = DialogSceneStrategy.dialog()
    ) {
        NetworkMocksWindow(
            fromNetworkCallId = it.id
        )
    }
    entry<NetworkRoutes.WindowDetail>(
        metadata = WindowSceneStrategy.window()
    ) {
        NetworkDetailScreen(requestId = it.requestId, key = it.windowKey)
    }
    entry<NetworkRoutes.JsonDetail>(
        metadata = WindowSceneStrategy.window()
    ) {
        NetworkBodyWindow(
            body = NetworkBodyDetailUi(text = it.json)
        )
    }
    entry<NetworkRoutes.DeepSearch>(
        metadata = WindowSceneStrategy.window(
            size = DpSize(
                width = 1200.0.dp,
                height = 800.0.dp
            )
        )
    ) {
        NetworkSearchScreen()
    }
}