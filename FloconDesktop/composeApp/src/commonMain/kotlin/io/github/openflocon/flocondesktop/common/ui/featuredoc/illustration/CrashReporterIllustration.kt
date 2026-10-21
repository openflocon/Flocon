package io.github.openflocon.flocondesktop.common.ui.featuredoc.illustration

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope

internal fun DrawScope.drawCrashReporter(c: ArtColors) {
    // stack trace card
    drawCard(c, Offset(28f, 30f), Size(70f, 62f))
    drawTextLine(c.error, Offset(36f, 40f), 40f)
    for (i in 0..4) {
        drawTextLine(c.line, Offset(42f, 50f + i * 8f), if (i % 2 == 0) 44f else 32f, thickness = 3f)
    }
    // bug
    val center = Offset(108f, 70f)
    listOf(-8f, 0f, 8f).forEach { y ->
        drawLine(c.strongLine, Offset(center.x - 18f, center.y + y), Offset(center.x + 18f, center.y + y), 2.5f, StrokeCap.Round)
    }
    drawLine(c.strongLine, Offset(center.x - 4f, center.y - 18f), Offset(center.x - 9f, center.y - 25f), 2.5f, StrokeCap.Round)
    drawLine(c.strongLine, Offset(center.x + 4f, center.y - 18f), Offset(center.x + 9f, center.y - 25f), 2.5f, StrokeCap.Round)
    drawOval(c.accent, Offset(center.x - 12f, center.y - 16f), Size(24f, 32f))
    drawCircle(c.strongLine, radius = 7f, center = Offset(center.x, center.y - 15f))
    drawLine(c.card.copy(alpha = 0.7f), Offset(center.x, center.y - 8f), Offset(center.x, center.y + 14f), 1.8f)
}
