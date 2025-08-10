package io.github.openflocon.flocondesktop.common.ui.window

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.app_icon
import org.jetbrains.compose.resources.painterResource

data class FloconWindowStateDesktop(
    val windowState: WindowState,
) : FloconWindowState

actual fun createFloconWindowState(): FloconWindowState = FloconWindowStateDesktop(
    WindowState(
        placement = WindowPlacement.Floating,
        position = WindowPosition(Alignment.Center),
    ),
)

@Composable
actual fun FloconWindow(
    title: String,
    state: FloconWindowState,
    onCloseRequest: () -> Unit,
    content: @Composable () -> Unit,
) {
    Window(
        title = title,
        icon = painterResource(Res.drawable.app_icon),
        state = (state as FloconWindowStateDesktop).windowState,
        onCloseRequest = onCloseRequest,
    ) {
        content()
    }
}
