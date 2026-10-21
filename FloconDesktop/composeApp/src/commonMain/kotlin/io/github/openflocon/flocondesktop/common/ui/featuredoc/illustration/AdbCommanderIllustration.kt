package io.github.openflocon.flocondesktop.common.ui.featuredoc.illustration

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope

internal fun DrawScope.drawAdbCommander(c: ArtColors) {
    val origin = Offset(30f, 30f)
    drawCard(c, origin, Size(100f, 64f))
    // window bar
    listOf(0, 1, 2).forEach { index ->
        drawCircle(if (index == 0) c.accent else c.line, radius = 2.5f, center = Offset(origin.x + 9f + index * 8f, origin.y + 8f))
    }
    drawLine(c.line, Offset(origin.x, origin.y + 15f), Offset(origin.x + 100f, origin.y + 15f), 1f)
    // prompt
    val promptY = origin.y + 30f
    drawLine(c.accent, Offset(origin.x + 10f, promptY - 5f), Offset(origin.x + 16f, promptY), 2.5f, StrokeCap.Round)
    drawLine(c.accent, Offset(origin.x + 16f, promptY), Offset(origin.x + 10f, promptY + 5f), 2.5f, StrokeCap.Round)
    drawTextLine(c.strongLine, Offset(origin.x + 22f, promptY), 46f, thickness = 3f)
    drawTextLine(c.line, Offset(origin.x + 10f, promptY + 12f), 60f, thickness = 3f)
    drawTextLine(c.line, Offset(origin.x + 10f, promptY + 22f), 40f, thickness = 3f)
    // cursor
    drawRect(c.accent, Offset(origin.x + 54f, promptY + 18f), Size(6f, 8f))
}
