package io.github.openflocon.flocondesktop.common.ui.window

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * How the app content draws the window title bar.
 *
 * When the system title bar is hidden (macOS), the content is drawn behind the window buttons:
 * [topInset] keeps the top bar below them, and [DragArea] lets the user move the window.
 */
@Immutable
class WindowTitleBar(
    val topInset: Dp,
    private val dragArea: @Composable (modifier: Modifier, content: @Composable () -> Unit) -> Unit,
) {
    @Composable
    fun DragArea(
        modifier: Modifier = Modifier,
        content: @Composable () -> Unit,
    ) = dragArea(modifier, content)

    companion object {
        // the system title bar is visible: nothing to do in the content
        val System = WindowTitleBar(
            topInset = 0.dp,
            dragArea = { modifier, content -> Box(modifier) { content() } },
        )
    }
}

val LocalWindowTitleBar = staticCompositionLocalOf { WindowTitleBar.System }
