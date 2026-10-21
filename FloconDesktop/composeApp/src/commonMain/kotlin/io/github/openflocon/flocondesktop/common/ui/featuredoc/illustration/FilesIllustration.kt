package io.github.openflocon.flocondesktop.common.ui.featuredoc.illustration

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope

internal fun DrawScope.drawFiles(c: ArtColors) {
    // documents sticking out of the folder
    drawCard(c, Offset(56f, 30f), Size(36f, 44f), radius = 4f)
    drawTextLine(c.line, Offset(62f, 40f), 22f, thickness = 3f)
    drawTextLine(c.line, Offset(62f, 48f), 16f, thickness = 3f)
    drawCard(c, Offset(72f, 26f), Size(36f, 44f), radius = 4f)
    drawTextLine(c.accentSoft, Offset(78f, 36f), 22f, thickness = 4f)
    drawTextLine(c.line, Offset(78f, 44f), 18f, thickness = 3f)
    // folder
    val folder = Path().apply {
        moveTo(38f, 56f)
        lineTo(38f, 92f)
        cubicTo(38f, 95f, 40f, 97f, 43f, 97f)
        lineTo(117f, 97f)
        cubicTo(120f, 97f, 122f, 95f, 122f, 92f)
        lineTo(122f, 62f)
        cubicTo(122f, 59f, 120f, 57f, 117f, 57f)
        lineTo(74f, 57f)
        lineTo(68f, 50f)
        lineTo(43f, 50f)
        cubicTo(40f, 50f, 38f, 52f, 38f, 56f)
        close()
    }
    drawPath(folder, color = c.accent)
    drawTextLine(c.card.copy(alpha = 0.6f), Offset(48f, 86f), 26f, thickness = 3f)
}
