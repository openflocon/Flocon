package io.github.openflocon.domain.common.ui.window

import androidx.compose.runtime.Composable

interface FloconWindowState

expect fun createFloconWindowState(): FloconWindowState

@Composable
expect fun FloconWindow(
    title: String,
    state: FloconWindowState,
    onCloseRequest: () -> Unit,
    content: @Composable () -> Unit,
)
