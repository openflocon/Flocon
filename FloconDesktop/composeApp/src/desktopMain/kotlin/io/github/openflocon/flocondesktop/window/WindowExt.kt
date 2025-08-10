package io.github.openflocon.flocondesktop.window

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import java.awt.Toolkit
import kotlin.math.min

fun WindowStateData?.windowPosition() = this?.let { WindowPosition(x.dp, y.dp) } ?: WindowPosition.PlatformDefault
fun WindowStateData?.size(): DpSize {
    val screenSize = Toolkit.getDefaultToolkit().screenSize

    val width = this?.width?.dp ?: min(
        DEFAULT_WINDOW_WIDTH,
        screenSize.width,
    ).dp
    val height = this?.height?.dp ?: min(
        DEFAULT_WINDOW_HEIGHT,
        screenSize.height,
    ).dp

    return DpSize(
        width = width, height = height,
    )
}
