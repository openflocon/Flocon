package io.github.openflocon.flocondesktop.window

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.v2.WindowBoundsProvider
import androidx.compose.ui.window.v2.WindowPositionProvider
import java.awt.Toolkit
import kotlin.math.min

fun WindowStateData?.windowPositionProvider(): WindowPositionProvider =
    this?.let { WindowPositionProvider.Absolute(x.dp, y.dp) } ?: WindowPositionProvider.Default

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

fun WindowStateData?.windowBoundsProvider(): WindowBoundsProvider =
    WindowBoundsProvider(
        positionProvider = windowPositionProvider(),
        sizeProvider = { size() },
    )
