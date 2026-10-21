package io.github.openflocon.flocondesktop.common.ui.featuredoc.illustration

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope

internal fun DrawScope.drawFeatureFlags(c: ArtColors) {
    drawCard(c, Offset(34f, 26f), Size(92f, 70f))
    // three flags with their switch, the middle one on
    for (i in 0..2) {
        val y = 40f + i * 20f
        drawTextLine(if (i == 1) c.strongLine else c.line, Offset(44f, y), 34f, thickness = 3.5f)
        val on = i == 1
        val track = Offset(96f, y - 5f)
        drawRoundRect(
            color = if (on) c.accent else c.line,
            topLeft = track,
            size = Size(20f, 10f),
            cornerRadius = CornerRadius(5f),
        )
        drawCircle(c.card, radius = 3.5f, center = Offset(if (on) track.x + 15f else track.x + 5f, y))
    }
    // flag on the corner
    drawLine(c.strongLine, Offset(126f, 18f), Offset(126f, 42f), 2f, StrokeCap.Round)
    val flag = Path().apply {
        moveTo(127f, 18f)
        lineTo(142f, 23f)
        lineTo(127f, 28f)
        close()
    }
    drawPath(flag, c.accent)
}
