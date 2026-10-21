package io.github.openflocon.flocondesktop.common.ui.featuredoc.illustration

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform

internal fun DrawScope.drawDeeplinks(c: ArtColors) {
    // chain
    withTransform({ rotate(degrees = -35f, pivot = Offset(80f, 62f)) }) {
        drawRoundRect(c.strongLine, Offset(44f, 52f), Size(42f, 20f), CornerRadius(10f), style = Stroke(5f))
        drawRoundRect(c.accent, Offset(74f, 52f), Size(42f, 20f), CornerRadius(10f), style = Stroke(5f))
    }
    // url bar
    drawCard(c, Offset(36f, 92f), Size(88f, 14f), radius = 7f)
    drawTextLine(c.accent, Offset(44f, 99f), 14f, thickness = 3f)
    drawTextLine(c.line, Offset(62f, 99f), 46f, thickness = 3f)
}
