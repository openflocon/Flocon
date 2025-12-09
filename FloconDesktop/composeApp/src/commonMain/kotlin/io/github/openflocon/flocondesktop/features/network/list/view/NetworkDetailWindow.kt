package io.github.openflocon.flocondesktop.features.network.list.view

import androidx.compose.runtime.Composable
import io.github.openflocon.flocondesktop.common.ui.window.FloconWindow
import io.github.openflocon.flocondesktop.common.ui.window.createFloconWindowState
import io.github.openflocon.flocondesktop.features.network.detail.view.NetworkDetailScreen

@Composable
fun NetworkDetailWindow(
    requestId: String,
    onCloseRequest: () -> Unit,
) {
    FloconWindow(
        title = "Network Detail",
        state = createFloconWindowState(),
        onCloseRequest = onCloseRequest,
    ) {
        NetworkDetailScreen(
            requestId = requestId,
        )
    }
}
