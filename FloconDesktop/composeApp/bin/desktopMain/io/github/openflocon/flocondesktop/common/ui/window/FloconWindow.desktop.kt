package io.github.openflocon.flocondesktop.common.ui.window

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.app_icon
import io.github.openflocon.library.designsystem.components.escape.LocalEscapeHandlerStack
import org.jetbrains.compose.resources.painterResource

data class FloconWindowStateDesktop(
    val windowState: WindowState,
) : FloconWindowState

actual fun createFloconWindowState(size: DpSize?): FloconWindowState = FloconWindowStateDesktop(
    WindowState(
        placement = WindowPlacement.Floating,
        position = WindowPosition(Alignment.Center),
        size = size ?: defaultWindowSize
    )
)

// TODO Use this component for main window too
@Composable
actual fun FloconWindow(
    title: String,
    state: FloconWindowState,
    onCloseRequest: () -> Unit,
    alwaysOnTop: Boolean,
    content: @Composable () -> Unit,
) {
    val handlers = remember { mutableStateListOf<() -> Boolean>() }

    Window(
        title = title,
        icon = painterResource(Res.drawable.app_icon),
        state = (state as FloconWindowStateDesktop).windowState,
        alwaysOnTop = alwaysOnTop,
        onPreviewKeyEvent = {
            when (it.key) {
                Key.Escape if (it.type == KeyEventType.KeyDown) -> handlers.lastOrNull()?.invoke() ?: false

                else -> false
            }
        },
        onCloseRequest = onCloseRequest,
    ) {
        CompositionLocalProvider(LocalEscapeHandlerStack provides handlers) {
            content()
        }
    }
}
