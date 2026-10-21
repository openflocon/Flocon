package io.github.openflocon.flocondesktop.common.ui.featuredoc.illustration

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import io.github.openflocon.library.designsystem.FloconTheme
import kotlin.math.min

// Every illustration is drawn in a 160 x 120 virtual canvas, then scaled to the available size
private const val ART_WIDTH = 160f
private const val ART_HEIGHT = 120f

@Immutable
internal data class ArtColors(
    val backdrop: Color,
    val card: Color,
    val cardStroke: Color,
    val line: Color,
    val strongLine: Color,
    val accent: Color,
    val accentSoft: Color,
    val error: Color,
)

@Composable
internal fun IllustrationCanvas(
    modifier: Modifier = Modifier,
    draw: DrawScope.(ArtColors) -> Unit,
) {
    val tokens = FloconTheme.colors
    val colors = ArtColors(
        backdrop = tokens.bg300,
        card = tokens.bg100,
        cardStroke = tokens.line,
        line = tokens.line,
        strongLine = tokens.inkFaint,
        accent = tokens.accent,
        accentSoft = tokens.accent.copy(alpha = 0.3f),
        error = tokens.statusError,
    )

    Canvas(modifier = modifier) {
        val scale = min(size.width / ART_WIDTH, size.height / ART_HEIGHT)
        val dx = (size.width - ART_WIDTH * scale) / 2f
        val dy = (size.height - ART_HEIGHT * scale) / 2f
        withTransform({
            translate(left = dx, top = dy)
            scale(scaleX = scale, scaleY = scale, pivot = Offset.Zero)
        }) {
            drawBackdrop(colors)
            draw(colors)
        }
    }
}

// shared shapes

private fun DrawScope.drawBackdrop(c: ArtColors) {
    drawCircle(color = c.backdrop, radius = 54f, center = Offset(80f, 62f))
    // a few sparkles around, the "flocon" touch
    drawSparkle(c.accent, Offset(24f, 22f), 4f)
    drawSparkle(c.strongLine, Offset(142f, 30f), 3f)
    drawSparkle(c.strongLine, Offset(18f, 96f), 2.5f)
}

internal fun DrawScope.drawSparkle(color: Color, center: Offset, radius: Float) {
    val stroke = radius / 2.2f
    drawLine(color, Offset(center.x - radius, center.y), Offset(center.x + radius, center.y), stroke, StrokeCap.Round)
    drawLine(color, Offset(center.x, center.y - radius), Offset(center.x, center.y + radius), stroke, StrokeCap.Round)
}

internal fun DrawScope.drawCard(
    c: ArtColors,
    topLeft: Offset,
    size: Size,
    radius: Float = 6f,
    fill: Color = c.card,
) {
    drawRoundRect(color = fill, topLeft = topLeft, size = size, cornerRadius = CornerRadius(radius))
    drawRoundRect(
        color = c.cardStroke,
        topLeft = topLeft,
        size = size,
        cornerRadius = CornerRadius(radius),
        style = Stroke(width = 1.2f),
    )
}

internal fun DrawScope.drawTextLine(
    color: Color,
    start: Offset,
    length: Float,
    thickness: Float = 4f,
) {
    drawLine(
        color = color,
        start = start,
        end = Offset(start.x + length, start.y),
        strokeWidth = thickness,
        cap = StrokeCap.Round,
    )
}

// endregion
