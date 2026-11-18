package io.github.openflocon.flocondesktop.common.ui.featuredoc.illustration

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope

internal fun DrawScope.drawTables(c: ArtColors) {
    val origin = Offset(30f, 30f)
    val size = Size(100f, 64f)
    drawCard(c, origin, size)
    // header
    drawRoundRect(c.accent, origin, Size(size.width, 14f), CornerRadius(6f))
    drawRect(c.accent, Offset(origin.x, origin.y + 8f), Size(size.width, 6f))
    // rows & columns
    for (i in 1..3) {
        val y = origin.y + 14f + i * 12.5f
        drawLine(c.line, Offset(origin.x, y), Offset(origin.x + size.width, y), 1f)
    }
    listOf(34f, 68f).forEach { x ->
        drawLine(c.line, Offset(origin.x + x, origin.y + 14f), Offset(origin.x + x, origin.y + size.height), 1f)
    }
    for (row in 0..3) {
        val y = origin.y + 20.5f + row * 12.5f
        drawTextLine(c.strongLine, Offset(origin.x + 6f, y), 20f, thickness = 3f)
        drawTextLine(c.line, Offset(origin.x + 40f, y), 22f, thickness = 3f)
        drawTextLine(if (row == 1) c.accent else c.line, Offset(origin.x + 74f, y), 18f, thickness = 3f)
    }
}
