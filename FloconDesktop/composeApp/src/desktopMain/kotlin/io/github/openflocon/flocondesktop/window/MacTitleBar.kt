package io.github.openflocon.flocondesktop.window

import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.FrameWindowScope
import io.github.openflocon.flocondesktop.common.ui.window.WindowTitleBar
import java.awt.Frame
import java.awt.event.MouseEvent

private val isMacOs: Boolean = "mac" in System.getProperty("os.name").lowercase()

// room for the close / minimize / zoom buttons drawn by macOS on top of the content
private val MacWindowButtonsHeight = 16.dp

/**
 * On macOS, hides the system title bar and draws the content behind the window buttons.
 * Other platforms keep their system title bar.
 */
@Composable
fun FrameWindowScope.rememberWindowTitleBar(): WindowTitleBar = remember(window) {
    if (!isMacOs) return@remember WindowTitleBar.System

    with(window.rootPane) {
        putClientProperty("apple.awt.fullWindowContent", true)
        putClientProperty("apple.awt.transparentTitleBar", true)
        putClientProperty("apple.awt.windowTitleVisible", false)
    }

    WindowTitleBar(
        topInset = MacWindowButtonsHeight,
        dragArea = { modifier, content ->
            WindowDraggableArea(
                modifier = modifier.pointerInput(Unit) {
                    // double click on the title bar zooms the window, like the system one
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            val awtEvent = event.nativeEvent as? MouseEvent ?: continue
                            if (event.type == PointerEventType.Release &&
                                awtEvent.clickCount == 2 &&
                                event.changes.none { it.isConsumed }
                            ) {
                                window.extendedState = if (window.extendedState and Frame.MAXIMIZED_BOTH != 0) {
                                    Frame.NORMAL
                                } else {
                                    Frame.MAXIMIZED_BOTH
                                }
                            }
                        }
                    }
                },
            ) {
                content()
            }
        },
    )
}
