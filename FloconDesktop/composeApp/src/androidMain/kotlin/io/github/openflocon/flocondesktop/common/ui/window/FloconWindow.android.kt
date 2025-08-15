package io.github.openflocon.flocondesktop.common.ui.window

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize

actual fun rememberFloconWindowState(
    placement: androidx.compose.ui.window.WindowPlacement,
    position: androidx.compose.ui.window.WindowPosition,
    size: DpSize
): FloconWindowState {
    TODO("Not yet implemented")
}

@Composable
actual fun FloconWindow(
    title: String,
    state: FloconWindowState,
    onCloseRequest: () -> Unit,
    content: @Composable (androidx.compose.ui.window.FrameWindowScope.() -> Unit)
) {
}
