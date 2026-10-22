@file:OptIn(ExperimentalUuidApi::class)

package io.github.openflocon.flocondesktop.features.network

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.metadata
import io.github.openflocon.flocondesktop.app.keepMenu
import io.github.openflocon.flocondesktop.app.menu
import io.github.openflocon.flocondesktop.features.network.body.NetworkDiffWindow
import io.github.openflocon.flocondesktop.features.network.body.NetworkJsonScreen
import io.github.openflocon.flocondesktop.features.network.body.model.NetworkDiffUi
import io.github.openflocon.flocondesktop.features.network.detail.view.NetworkDetailScreen
import io.github.openflocon.flocondesktop.features.network.list.NetworkViewModel
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkAction
import io.github.openflocon.flocondesktop.features.network.list.view.NetworkScreen
import io.github.openflocon.flocondesktop.features.network.mock.list.view.NetworkMocksWindow
import io.github.openflocon.flocondesktop.features.network.search.view.NetworkSearchScreen
import io.github.openflocon.navigation.FloconRoute
import io.github.openflocon.navigation.PanelRoute
import io.github.openflocon.navigation.WindowRoute
import io.github.openflocon.navigation.scene.BigDialogProperties
import io.github.openflocon.navigation.scene.BigDialogSceneStrategy
import io.github.openflocon.navigation.scene.OnPin
import io.github.openflocon.navigation.scene.PanelSceneStrategy.Companion.panel
import io.github.openflocon.navigation.scene.WindowProperties
import io.github.openflocon.navigation.scene.WindowSceneStrategy
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
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

    @Serializable
    data class Diff(
        val json: String,
        val clipboardJson: String,
    ) : NetworkRoutes
}

fun EntryProviderScope<FloconRoute>.networkRoutes() {
    entry<NetworkRoutes.Main>(
        metadata = metadata {
            menu()
        }
    ) {
        NetworkScreen()
    }
    entry<NetworkRoutes.Panel>(
        metadata = metadata {
            panel(
                closable = true,
                onPinHandler = {
                    val viewModel = koinViewModel<NetworkViewModel>()

                    object : OnPin {
                        override val isPinned: StateFlow<Boolean> = viewModel.isPinned

                        override fun onPin(pinned: Boolean) {
                            viewModel.onAction(NetworkAction.Pinned(pinned))
                        }
                    }
                }
            )
            keepMenu()
        }
    ) {
        NetworkDetailScreen(requestId = it.requestId)
    }
    entry<NetworkRoutes.Mocks>(
        metadata = BigDialogSceneStrategy.bigDialog(
            BigDialogProperties(title = "Mocks")
        )
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
        metadata = WindowSceneStrategy.window(
            WindowProperties(
                title = "Body",
                size = DpSize(
                    width = 900.0.dp,
                    height = 700.0.dp
                )
            )
        )
    ) {
        NetworkJsonScreen(
            json = it.json,
            key = it.id,
        )
    }

    entry<NetworkRoutes.Diff>(
        metadata = WindowSceneStrategy.window(WindowProperties(title = "Diff"))
    ) {
        NetworkDiffWindow(
            diff = NetworkDiffUi(
                json = it.json,
                clipboardJson = it.clipboardJson
            )
        )
    }
    entry<NetworkRoutes.DeepSearch>(
        metadata = WindowSceneStrategy.window(
            WindowProperties(
                size = DpSize(
                    width = 1200.0.dp,
                    height = 800.0.dp
                )
            )
        )
    ) {
        NetworkSearchScreen()
    }
}
