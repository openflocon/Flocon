package io.github.openflocon.flocondesktop.features.timeline.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.timeline_log_level_debug
import flocondesktop.composeapp.generated.resources.timeline_log_level_error
import flocondesktop.composeapp.generated.resources.timeline_log_level_info
import flocondesktop.composeapp.generated.resources.timeline_log_level_warning
import io.github.openflocon.domain.timelinelog.models.TimelineLogLevel
import io.github.openflocon.flocondesktop.features.timeline.mapper.LogGlyph
import io.github.openflocon.flocondesktop.features.timeline.mapper.LogGlyphShape
import io.github.openflocon.flocondesktop.features.timeline.mapper.logGlyphOf
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

internal fun TimelineLogLevel.label(): StringResource = when (this) {
    TimelineLogLevel.Debug -> Res.string.timeline_log_level_debug
    TimelineLogLevel.Info -> Res.string.timeline_log_level_info
    TimelineLogLevel.Warning -> Res.string.timeline_log_level_warning
    TimelineLogLevel.Error -> Res.string.timeline_log_level_error
}

// the glyphs are drawn in a 24x24 box, the same drawing for the list, the graph and the detail
private const val GlyphBox = 24f

private class GlyphDrawing(
    path: String,
    // filled shapes can also have an outline (round joins) and the arrow is only a stroke
    val filled: Boolean,
    val strokeWidth: Float,
) {
    val path: Path = PathParser().parsePathString(path).toPath()
}

private val ArrowDrawing = GlyphDrawing("M5 12h14M13 6l6 6-6 6", filled = false, strokeWidth = 2.4f)
private val PointerDrawing = GlyphDrawing("M5 3l14 7.5-6.2 1.9L10.6 19z", filled = true, strokeWidth = 1.6f)
private val HeartDrawing = GlyphDrawing(
    "M12 20.5s-7.5-4.6-7.5-10.3A4.2 4.2 0 0 1 12 7.6a4.2 4.2 0 0 1 7.5 2.6c0 5.7-7.5 10.3-7.5 10.3z",
    filled = true,
    strokeWidth = 0f,
)
private val DiamondDrawing = GlyphDrawing("M12 3l9 9-9 9-9-9z", filled = true, strokeWidth = 0f)

private fun LogGlyphShape.drawing(): GlyphDrawing = when (this) {
    LogGlyphShape.Arrow -> ArrowDrawing
    LogGlyphShape.Pointer -> PointerDrawing
    LogGlyphShape.Heart -> HeartDrawing
    LogGlyphShape.Diamond -> DiamondDrawing
}

/**
 * Draws a [shape] centered on [center] in a square of [boxPx] px: the one drawing of the list, the graph and the detail.
 * [outline] strokes the shape, ex: the selected marker of the graph.
 */
internal fun DrawScope.drawLogGlyph(
    shape: LogGlyphShape,
    color: Color,
    center: Offset,
    boxPx: Float,
    outline: Color? = null,
    outlinePx: Float = 2f,
) {
    val drawing = shape.drawing()
    val scale = boxPx / GlyphBox
    withTransform({
        translate(left = center.x - boxPx / 2f, top = center.y - boxPx / 2f)
        scale(scaleX = scale, scaleY = scale, pivot = Offset.Zero)
    }) {
        if (drawing.filled) drawPath(drawing.path, color)
        if (drawing.strokeWidth > 0f) {
            drawPath(drawing.path, color, style = Stroke(drawing.strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
        }
        if (outline != null) {
            drawPath(drawing.path, outline, style = Stroke(outlinePx / scale, cap = StrokeCap.Round, join = StrokeJoin.Round))
        }
    }
}

/**
 * The glyph of a log, shape and color coming from [logGlyphOf]. Without [contentDescription] it is decorative.
 */
@Composable
internal fun LogGlyphIcon(
    glyph: LogGlyph,
    modifier: Modifier = Modifier,
    size: Dp = LogIconSize,
    contentDescription: String? = null,
) {
    val color = glyph.color(FloconTheme.colors)
    Canvas(
        modifier = modifier
            .size(size)
            .then(if (contentDescription != null) Modifier.semantics { this.contentDescription = contentDescription } else Modifier),
    ) {
        drawLogGlyph(glyph.shape, color, center = center, boxPx = this.size.minDimension)
    }
}

// the shape tells the type of the log, its color the level (or the type): the level is also its description
@Composable
internal fun LogTypeIcon(
    type: String,
    level: TimelineLogLevel,
    modifier: Modifier = Modifier,
    size: Dp = LogIconSize,
) {
    val glyph = remember(type, level) { logGlyphOf(type, level) }
    LogGlyphIcon(
        glyph = glyph,
        modifier = modifier,
        size = size,
        contentDescription = if (glyph.isTyped) type else stringResource(level.label()),
    )
}

private val LogIconSize = 14.dp

// every shape and every level, in both themes
@Composable
private fun LogGlyphsPreviewContent(isDarkTheme: Boolean) {
    FloconTheme(isDarkTheme = isDarkTheme) {
        Row(
            modifier = Modifier.background(FloconTheme.colors.bg100).padding(FloconTheme.spacing.medium),
            horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            listOf("navigation", "userAction", "lifecycle").forEach { LogTypeIcon(type = it, level = TimelineLogLevel.Info, size = 24.dp) }
            TimelineLogLevel.entries.forEach { LogTypeIcon(type = "billing", level = it, size = 24.dp) }
        }
    }
}

@Preview
@Composable
private fun LogGlyphsDarkPreview() = LogGlyphsPreviewContent(isDarkTheme = true)

@Preview
@Composable
private fun LogGlyphsLightPreview() = LogGlyphsPreviewContent(isDarkTheme = false)
