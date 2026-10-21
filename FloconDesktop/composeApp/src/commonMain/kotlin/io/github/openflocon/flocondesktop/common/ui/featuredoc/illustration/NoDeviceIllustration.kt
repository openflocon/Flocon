package io.github.openflocon.flocondesktop.common.ui.featuredoc.illustration

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope

internal fun DrawScope.drawNoDevice(c: ArtColors) {
    // phone, screen off
    drawCard(c, Offset(34f, 28f), Size(40f, 66f), radius = 8f)
    drawTextLine(c.line, Offset(47f, 34f), 14f, thickness = 2.5f)
    drawCircle(c.line, radius = 2.5f, center = Offset(54f, 86f))
    // desktop
    drawCard(c, Offset(92f, 38f), Size(46f, 34f), radius = 4f)
    drawTextLine(c.line, Offset(99f, 48f), 24f, thickness = 3f)
    drawTextLine(c.line, Offset(99f, 57f), 16f, thickness = 3f)
    drawLine(c.strongLine, Offset(115f, 72f), Offset(115f, 80f), 2f)
    drawTextLine(c.strongLine, Offset(106f, 80f), 18f, thickness = 2.5f)
    // broken link between them
    val dashed = PathEffect.dashPathEffect(floatArrayOf(4f, 4f))
    drawLine(c.strongLine, Offset(78f, 58f), Offset(88f, 58f), 2f, StrokeCap.Round, pathEffect = dashed)
    drawCircle(c.accent, radius = 7f, center = Offset(83f, 44f))
    drawLine(c.card, Offset(80f, 41f), Offset(86f, 47f), 2f, StrokeCap.Round)
    drawLine(c.card, Offset(86f, 41f), Offset(80f, 47f), 2f, StrokeCap.Round)
}
