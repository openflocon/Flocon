package io.github.openflocon.flocondesktop.common.ui.featuredoc

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.dp
import io.github.openflocon.library.designsystem.FloconTheme
import androidx.compose.ui.tooling.preview.Preview
import kotlin.math.min

// Every illustration is drawn in a 160 x 120 virtual canvas, then scaled to the available size
private const val ART_WIDTH = 160f
private const val ART_HEIGHT = 120f

@Immutable
private data class ArtColors(
    val backdrop: Color,
    val card: Color,
    val cardStroke: Color,
    val line: Color,
    val strongLine: Color,
    val accent: Color,
    val accentSoft: Color,
    val error: Color,
)

/**
 * Flat, theme aware illustration dedicated to each page of the desktop app.
 */
@Composable
fun FeatureIllustration(
    doc: FeatureDoc,
    modifier: Modifier = Modifier,
) {
    IllustrationCanvas(modifier = modifier) { colors ->
        when (doc) {
            FeatureDoc.Network -> drawNetwork(colors)
            FeatureDoc.Images -> drawImages(colors)
            FeatureDoc.Database -> drawDatabase(colors)
            FeatureDoc.SharedPreferences -> drawPreferences(colors)
            FeatureDoc.Files -> drawFiles(colors)
            FeatureDoc.Dashboard -> drawDashboard(colors)
            FeatureDoc.Analytics -> drawAnalytics(colors)
            FeatureDoc.Tables -> drawTables(colors)
            FeatureDoc.Deeplinks -> drawDeeplinks(colors)
            FeatureDoc.CrashReporter -> drawCrashReporter(colors)
            FeatureDoc.AdbCommander -> drawAdbCommander(colors)
        }
    }
}

/**
 * Illustration displayed instead of the page one when no device is connected yet.
 */
@Composable
fun NoDeviceIllustration(
    modifier: Modifier = Modifier,
) {
    IllustrationCanvas(modifier = modifier) { colors ->
        drawNoDevice(colors)
    }
}

@Composable
private fun IllustrationCanvas(
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

// region shared shapes

private fun DrawScope.drawBackdrop(c: ArtColors) {
    drawCircle(color = c.backdrop, radius = 54f, center = Offset(80f, 62f))
    // a few sparkles around, the "flocon" touch
    drawSparkle(c.accent, Offset(24f, 22f), 4f)
    drawSparkle(c.strongLine, Offset(142f, 30f), 3f)
    drawSparkle(c.strongLine, Offset(18f, 96f), 2.5f)
}

private fun DrawScope.drawSparkle(color: Color, center: Offset, radius: Float) {
    val stroke = radius / 2.2f
    drawLine(color, Offset(center.x - radius, center.y), Offset(center.x + radius, center.y), stroke, StrokeCap.Round)
    drawLine(color, Offset(center.x, center.y - radius), Offset(center.x, center.y + radius), stroke, StrokeCap.Round)
}

private fun DrawScope.drawCard(
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

private fun DrawScope.drawTextLine(
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

private fun DrawScope.drawNetwork(c: ArtColors) {
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

private fun DrawScope.drawImages(c: ArtColors) {
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

private fun DrawScope.drawDatabase(c: ArtColors) {
    val left = 52f
    val width = 56f
    val ellipseHeight = 16f
    val levels = listOf(30f, 50f, 70f)
    // body
    drawRect(c.card, topLeft = Offset(left, 38f), size = Size(width, 48f))
    drawLine(c.cardStroke, Offset(left, 38f), Offset(left, 86f), 1.2f)
    drawLine(c.cardStroke, Offset(left + width, 38f), Offset(left + width, 86f), 1.2f)
    // bottom
    drawArc(c.card, 0f, 180f, true, Offset(left, 78f), Size(width, ellipseHeight))
    drawArc(c.cardStroke, 0f, 180f, false, Offset(left, 78f), Size(width, ellipseHeight), style = Stroke(1.2f))
    // separations
    levels.drop(1).forEach { top ->
        drawArc(c.strongLine, 0f, 180f, false, Offset(left, top), Size(width, ellipseHeight), style = Stroke(1.6f))
    }
    // top
    drawOval(c.card, Offset(left, levels.first()), Size(width, ellipseHeight))
    drawOval(c.accentSoft, Offset(left, levels.first()), Size(width, ellipseHeight))
    drawOval(c.strongLine, Offset(left, levels.first()), Size(width, ellipseHeight), style = Stroke(1.6f))
    // query lens
    drawCircle(c.card, radius = 11f, center = Offset(112f, 82f))
    drawCircle(c.accent, radius = 11f, center = Offset(112f, 82f), style = Stroke(3f))
    drawLine(c.accent, Offset(120f, 90f), Offset(128f, 98f), 4f, StrokeCap.Round)
}

private fun DrawScope.drawPreferences(c: ArtColors) {
    drawCard(c, Offset(36f, 28f), Size(88f, 68f))
    val rows = listOf(40f, 58f, 76f)
    rows.forEachIndexed { index, y ->
        drawTextLine(c.strongLine, Offset(46f, y), if (index == 1) 22f else 30f)
        val enabled = index != 1
        val trackColor = if (enabled) c.accent else c.line
        drawRoundRect(trackColor, Offset(94f, y - 5f), Size(20f, 10f), CornerRadius(5f))
        drawCircle(c.card, radius = 3.6f, center = Offset(if (enabled) 109f else 99f, y))
    }
    // key
    drawCircle(c.accent, radius = 7f, center = Offset(132f, 92f), style = Stroke(3f))
    drawLine(c.accent, Offset(126f, 97f), Offset(116f, 107f), 3f, StrokeCap.Round)
    drawLine(c.accent, Offset(119f, 104f), Offset(122f, 107f), 3f, StrokeCap.Round)
}

private fun DrawScope.drawFiles(c: ArtColors) {
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

private fun DrawScope.drawDashboard(c: ArtColors) {
    // gauge card
    drawCard(c, Offset(32f, 28f), Size(46f, 40f))
    drawArc(c.line, 180f, 180f, false, Offset(41f, 40f), Size(28f, 28f), style = Stroke(5f, cap = StrokeCap.Round))
    drawArc(c.accent, 180f, 120f, false, Offset(41f, 40f), Size(28f, 28f), style = Stroke(5f, cap = StrokeCap.Round))
    // button card
    drawCard(c, Offset(84f, 28f), Size(46f, 18f))
    drawRoundRect(c.accent, Offset(90f, 33f), Size(34f, 8f), CornerRadius(4f))
    // text field card
    drawCard(c, Offset(84f, 50f), Size(46f, 18f))
    drawTextLine(c.strongLine, Offset(90f, 59f), 20f, thickness = 3f)
    // bars card
    drawCard(c, Offset(32f, 74f), Size(98f, 24f))
    listOf(10f, 16f, 8f, 14f, 18f, 12f).forEachIndexed { index, height ->
        val x = 42f + index * 14f
        drawLine(
            color = if (index == 4) c.accent else c.strongLine,
            start = Offset(x, 94f),
            end = Offset(x, 94f - height),
            strokeWidth = 5f,
            cap = StrokeCap.Round,
        )
    }
}

private fun DrawScope.drawAnalytics(c: ArtColors) {
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

private fun DrawScope.drawTables(c: ArtColors) {
    val origin = Offset(30f, 30f)
    val size = Size(100f, 64f)
    drawCard(c, origin, size)
    // header
    drawRoundRect(c.accent, origin, Size(size.width, 14f), CornerRadius(6f))
    drawRect(c.accent, Offset(origin.x, origin.y + 8f), Size(size.width, 6f))
    // rows & columns
    for (i in 1..3) {
        val y = origin.y + 14f + i * 12.5f
        drawLine(c.line, Offset(origin.x, y), Offset(origin.x + size.width, y), 1f)
    }
    listOf(34f, 68f).forEach { x ->
        drawLine(c.line, Offset(origin.x + x, origin.y + 14f), Offset(origin.x + x, origin.y + size.height), 1f)
    }
    for (row in 0..3) {
        val y = origin.y + 20.5f + row * 12.5f
        drawTextLine(c.strongLine, Offset(origin.x + 6f, y), 20f, thickness = 3f)
        drawTextLine(c.line, Offset(origin.x + 40f, y), 22f, thickness = 3f)
        drawTextLine(if (row == 1) c.accent else c.line, Offset(origin.x + 74f, y), 18f, thickness = 3f)
    }
}

private fun DrawScope.drawDeeplinks(c: ArtColors) {
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

private fun DrawScope.drawCrashReporter(c: ArtColors) {
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

private fun DrawScope.drawAdbCommander(c: ArtColors) {
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

private fun DrawScope.drawNoDevice(c: ArtColors) {
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

@Preview
@Composable
private fun FeatureIllustrationsPreview() {
    FloconTheme {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            FeatureDoc.entries.forEach {
                FeatureIllustration(doc = it, modifier = Modifier.size(width = 160.dp, height = 120.dp))
            }
            NoDeviceIllustration(modifier = Modifier.size(width = 160.dp, height = 120.dp))
        }
    }
}
