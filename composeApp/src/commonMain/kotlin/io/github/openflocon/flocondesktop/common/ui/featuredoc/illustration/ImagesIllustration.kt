package io.github.openflocon.flocondesktop.common.ui.featuredoc.illustration

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform

internal fun DrawScope.drawImages(c: ArtColors) {
    // back photo, slightly rotated
    withTransform({ rotate(degrees = -10f, pivot = Offset(70f, 60f)) }) {
        drawCard(c, Offset(40f, 30f), Size(64f, 50f))
    }
    // front photo
    val origin = Offset(56f, 40f)
    drawCard(c, origin, Size(68f, 54f))
    // sun
    drawCircle(c.accent, radius = 6f, center = Offset(origin.x + 50f, origin.y + 14f))
    // mountains
    val mountains = Path().apply {
        moveTo(origin.x + 6f, origin.y + 48f)
        lineTo(origin.x + 26f, origin.y + 22f)
        lineTo(origin.x + 38f, origin.y + 36f)
        lineTo(origin.x + 46f, origin.y + 28f)
        lineTo(origin.x + 62f, origin.y + 48f)
        close()
    }
    drawPath(mountains, color = c.strongLine)
}
