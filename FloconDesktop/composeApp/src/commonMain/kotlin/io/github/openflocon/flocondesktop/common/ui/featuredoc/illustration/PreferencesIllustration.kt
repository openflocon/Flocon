package io.github.openflocon.flocondesktop.common.ui.featuredoc.illustration

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

internal fun DrawScope.drawPreferences(c: ArtColors) {
    drawCard(c, Offset(36f, 28f), Size(88f, 68f))
    val rows = listOf(40f, 58f, 76f)
    rows.forEachIndexed { index, y ->
        drawTextLine(c.strongLine, Offset(46f, y), if (index == 1) 22f else 30f)
        val enabled = index != 1
        val trackColor = if (enabled) c.accent else c.line
        drawRoundRect(trackColor, Offset(94f, y - 5f), Size(20f, 10f), CornerRadius(5f))
        drawCircle(c.card, radius = 3.6f, center = Offset(if (enabled) 109f else 99f, y))
    }
    // key
    drawCircle(c.accent, radius = 7f, center = Offset(132f, 92f), style = Stroke(3f))
    drawLine(c.accent, Offset(126f, 97f), Offset(116f, 107f), 3f, StrokeCap.Round)
    drawLine(c.accent, Offset(119f, 104f), Offset(122f, 107f), 3f, StrokeCap.Round)
}
