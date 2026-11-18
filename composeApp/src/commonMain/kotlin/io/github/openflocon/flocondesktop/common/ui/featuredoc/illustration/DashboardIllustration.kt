package io.github.openflocon.flocondesktop.common.ui.featuredoc.illustration

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

internal fun DrawScope.drawDashboard(c: ArtColors) {
    // gauge card
    drawCard(c, Offset(32f, 28f), Size(46f, 40f))
    drawArc(c.line, 180f, 180f, false, Offset(41f, 40f), Size(28f, 28f), style = Stroke(5f, cap = StrokeCap.Round))
    drawArc(c.accent, 180f, 120f, false, Offset(41f, 40f), Size(28f, 28f), style = Stroke(5f, cap = StrokeCap.Round))
    // button card
    drawCard(c, Offset(84f, 28f), Size(46f, 18f))
    drawRoundRect(c.accent, Offset(90f, 33f), Size(34f, 8f), CornerRadius(4f))
    // text field card
    drawCard(c, Offset(84f, 50f), Size(46f, 18f))
    drawTextLine(c.strongLine, Offset(90f, 59f), 20f, thickness = 3f)
    // bars card
    drawCard(c, Offset(32f, 74f), Size(98f, 24f))
    listOf(10f, 16f, 8f, 14f, 18f, 12f).forEachIndexed { index, height ->
        val x = 42f + index * 14f
        drawLine(
            color = if (index == 4) c.accent else c.strongLine,
            start = Offset(x, 94f),
            end = Offset(x, 94f - height),
            strokeWidth = 5f,
            cap = StrokeCap.Round,
        )
    }
}
