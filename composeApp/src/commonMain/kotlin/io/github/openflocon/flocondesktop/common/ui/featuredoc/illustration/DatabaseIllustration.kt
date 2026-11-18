package io.github.openflocon.flocondesktop.common.ui.featuredoc.illustration

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

internal fun DrawScope.drawDatabase(c: ArtColors) {
    val left = 52f
    val width = 56f
    val ellipseHeight = 16f
    val levels = listOf(30f, 50f, 70f)
    // body
    drawRect(c.card, topLeft = Offset(left, 38f), size = Size(width, 48f))
    drawLine(c.cardStroke, Offset(left, 38f), Offset(left, 86f), 1.2f)
    drawLine(c.cardStroke, Offset(left + width, 38f), Offset(left + width, 86f), 1.2f)
    // bottom
    drawArc(c.card, 0f, 180f, true, Offset(left, 78f), Size(width, ellipseHeight))
    drawArc(c.cardStroke, 0f, 180f, false, Offset(left, 78f), Size(width, ellipseHeight), style = Stroke(1.2f))
    // separations
    levels.drop(1).forEach { top ->
        drawArc(c.strongLine, 0f, 180f, false, Offset(left, top), Size(width, ellipseHeight), style = Stroke(1.6f))
    }
    // top
    drawOval(c.card, Offset(left, levels.first()), Size(width, ellipseHeight))
    drawOval(c.accentSoft, Offset(left, levels.first()), Size(width, ellipseHeight))
    drawOval(c.strongLine, Offset(left, levels.first()), Size(width, ellipseHeight), style = Stroke(1.6f))
    // query lens
    drawCircle(c.card, radius = 11f, center = Offset(112f, 82f))
    drawCircle(c.accent, radius = 11f, center = Offset(112f, 82f), style = Stroke(3f))
    drawLine(c.accent, Offset(120f, 90f), Offset(128f, 98f), 4f, StrokeCap.Round)
}
