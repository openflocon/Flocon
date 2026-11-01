package io.github.openflocon.flocondesktop.common.ui

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

private val L_CORNER_RADIUS = 12.dp

/**
 * Top bar and sidebar in a single block, an inverted L:
 * the bar spans the whole width, the sidebar goes down on the left,
 * with a concave corner where they meet.
 */
internal class LShape(
    private val sidebarWidth: Dp,
    private val barHeight: Dp,
) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val r = with(density) { L_CORNER_RADIUS.toPx() }
        val sw = with(density) { sidebarWidth.toPx() }
        val bh = with(density) { barHeight.toPx() }
        val w = size.width
        val h = size.height
        return Outline.Generic(
            Path().apply {
                moveTo(r, 0f)
                lineTo(w - r, 0f)
                quadraticTo(w, 0f, w, r)
                lineTo(w, bh - r)
                quadraticTo(w, bh, w - r, bh)
                lineTo(sw + r, bh)
                quadraticTo(sw, bh, sw, bh + r)
                lineTo(sw, h - r)
                quadraticTo(sw, h, sw - r, h)
                lineTo(r, h)
                quadraticTo(0f, h, 0f, h - r)
                lineTo(0f, r)
                quadraticTo(0f, 0f, r, 0f)
                close()
            }
        )
    }
}
