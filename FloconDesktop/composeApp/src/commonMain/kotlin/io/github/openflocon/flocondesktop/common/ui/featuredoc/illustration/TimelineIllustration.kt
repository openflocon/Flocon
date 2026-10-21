package io.github.openflocon.flocondesktop.common.ui.featuredoc.illustration

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

internal fun DrawScope.drawTimeline(c: ArtColors) {
    // vertical axis with events on it
    drawLine(c.strongLine, Offset(52f, 22f), Offset(52f, 100f), 2f, StrokeCap.Round)
    listOf(30f, 52f, 74f, 94f).forEachIndexed { index, y ->
        val highlight = index == 2
        drawCircle(if (highlight) c.accent else c.card, radius = 5f, center = Offset(52f, y))
        drawCircle(if (highlight) c.accent else c.strongLine, radius = 5f, center = Offset(52f, y), style = Stroke(width = 1.5f))
        drawCard(c, Offset(64f, y - 8f), Size(if (highlight) 70f else 58f, 16f), radius = 4f)
        drawTextLine(if (highlight) c.accentSoft else c.line, Offset(71f, y), if (highlight) 48f else 36f, thickness = 4f)
    }
    // time marks
    drawTextLine(c.line, Offset(26f, 30f), 14f, thickness = 2.5f)
    drawTextLine(c.line, Offset(26f, 74f), 14f, thickness = 2.5f)
}
