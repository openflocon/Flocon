package io.github.openflocon.flocondesktop.common.ui.window

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

interface FloconWindowState

val defaultWindowSize = DpSize(800.dp, 600.dp)

expect fun createFloconWindowState(
    size: DpSize? = null,
): FloconWindowState

@Composable
expect fun FloconWindow(
    title: String,
    state: FloconWindowState,
    onCloseRequest: () -> Unit,
    content: @Composable () -> Unit,
)
