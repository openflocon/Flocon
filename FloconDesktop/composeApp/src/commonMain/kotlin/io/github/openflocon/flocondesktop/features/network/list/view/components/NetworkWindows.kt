package io.github.openflocon.flocondesktop.features.network.list.view.components

import androidx.compose.runtime.Composable
import io.github.openflocon.flocondesktop.features.network.badquality.list.view.BadNetworkQualityWindow
import io.github.openflocon.flocondesktop.features.network.body.model.ContentUiState
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkAction
import io.github.openflocon.flocondesktop.features.network.websocket.NetworkWebsocketMockWindow

/** Windows opened from the network screen (bad quality, websocket mocks). */
@Composable
fun NetworkWindows(
    contentState: ContentUiState,
    onAction: (NetworkAction) -> Unit,
) {
    if (contentState.badNetworkQualityDisplayed) {
        BadNetworkQualityWindow(
            onCloseRequest = { onAction(NetworkAction.CloseBadNetworkQuality) },
        )
    }

    if (contentState.websocketMocksDisplayed) {
        NetworkWebsocketMockWindow(
            onCloseRequest = { onAction(NetworkAction.CloseWebsocketMocks) }
        )
    }
}
