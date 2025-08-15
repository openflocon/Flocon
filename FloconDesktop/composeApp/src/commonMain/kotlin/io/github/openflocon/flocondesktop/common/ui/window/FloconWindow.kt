package io.github.openflocon.flocondesktop.common.ui.window

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition

interface FloconWindowState

expect fun rememberFloconWindowState(
    placement: WindowPlacement = WindowPlacement.Floating,
    position: WindowPosition = WindowPosition(Alignment.Center),
    size: DpSize = DpSize.Unspecified
): FloconWindowState

@Composable
expect fun FloconWindow(
    title: String,
    state: FloconWindowState,
    onCloseRequest: () -> Unit,
    content: @Composable FrameWindowScope.() -> Unit,
)
