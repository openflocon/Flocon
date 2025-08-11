package io.github.openflocon.domain.common.ui.window

import androidx.compose.runtime.Composable
import io.github.openflocon.flocondesktop.common.ui.window.FloconWindowState

actual fun createFloconWindowState(): FloconWindowState {
    TODO("Not yet implemented")
}

@Composable
actual fun FloconWindow(
    title: String,
    state: FloconWindowState,
    onCloseRequest: () -> Unit,
    content: @Composable () -> Unit,
) {
    TODO()
}
