package io.github.openflocon.flocondesktop.common.ui.window

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize

actual fun createFloconWindowState(
    size: DpSize?,
): FloconWindowState {
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
