package io.github.openflocon.flocondesktop.common.ui.featuredoc.illustration

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope

internal fun DrawScope.drawNetwork(c: ArtColors) {
    // phone
    drawCard(c, Offset(28f, 34f), Size(34f, 58f), radius = 7f)
    drawTextLine(c.line, Offset(39f, 40f), 12f, thickness = 2.5f)
    drawTextLine(c.line, Offset(35f, 52f), 20f)
    drawTextLine(c.line, Offset(35f, 60f), 14f)
    drawTextLine(c.accentSoft, Offset(35f, 70f), 20f, thickness = 6f)
    // server
    for (i in 0..2) {
        val top = 38f + i * 18f
        drawCard(c, Offset(100f, top), Size(36f, 14f), radius = 4f)
        drawCircle(if (i == 1) c.accent else c.strongLine, radius = 2f, center = Offset(107f, top + 7f))
        drawTextLine(c.line, Offset(114f, top + 7f), 14f, thickness = 3f)
    }
    // request / response arrows
    val dashed = PathEffect.dashPathEffect(floatArrayOf(4f, 4f))
    drawLine(c.strongLine, Offset(68f, 52f), Offset(94f, 52f), 2f, StrokeCap.Round, pathEffect = dashed)
    drawLine(c.strongLine, Offset(94f, 52f), Offset(90f, 48f), 2f, StrokeCap.Round)
    drawLine(c.strongLine, Offset(94f, 52f), Offset(90f, 56f), 2f, StrokeCap.Round)
    drawLine(c.accent, Offset(94f, 72f), Offset(68f, 72f), 2f, StrokeCap.Round, pathEffect = dashed)
    drawLine(c.accent, Offset(68f, 72f), Offset(72f, 68f), 2f, StrokeCap.Round)
    drawLine(c.accent, Offset(68f, 72f), Offset(72f, 76f), 2f, StrokeCap.Round)
}
