package io.github.openflocon.flocondesktop.common.ui.featuredoc.illustration

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

internal fun DrawScope.drawAnalytics(c: ArtColors) {
    drawCard(c, Offset(32f, 28f), Size(96f, 70f))
    // axis
    drawLine(c.line, Offset(42f, 88f), Offset(118f, 88f), 1.5f, StrokeCap.Round)
    val heights = listOf(16f, 26f, 20f, 34f, 44f)
    heights.forEachIndexed { index, height ->
        val x = 50f + index * 15f
        drawRoundRect(
            color = if (index == heights.lastIndex) c.accent else c.accentSoft,
            topLeft = Offset(x - 5f, 86f - height),
            size = Size(10f, height),
            cornerRadius = CornerRadius(2.5f),
        )
    }
    // trend
    val trend = Path().apply {
        moveTo(50f, 62f)
        lineTo(65f, 54f)
        lineTo(80f, 58f)
        lineTo(95f, 46f)
        lineTo(110f, 36f)
    }
    drawPath(trend, color = c.strongLine, style = Stroke(2f, cap = StrokeCap.Round))
    drawCircle(c.card, radius = 3.5f, center = Offset(110f, 36f))
    drawCircle(c.strongLine, radius = 3.5f, center = Offset(110f, 36f), style = Stroke(2f))
}
