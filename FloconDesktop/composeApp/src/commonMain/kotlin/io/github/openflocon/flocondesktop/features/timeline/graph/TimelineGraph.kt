package io.github.openflocon.flocondesktop.features.timeline.graph

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorFilter.Companion.tint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.isCtrlPressed
import androidx.compose.ui.input.pointer.isMetaPressed
import androidx.compose.ui.input.pointer.isShiftPressed
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.timeline_graph_description
import flocondesktop.composeapp.generated.resources.timeline_graph_empty
import flocondesktop.composeapp.generated.resources.timeline_graph_fit
import flocondesktop.composeapp.generated.resources.timeline_graph_latest
import flocondesktop.composeapp.generated.resources.timeline_graph_legend_bar
import flocondesktop.composeapp.generated.resources.timeline_graph_legend_failed
import flocondesktop.composeapp.generated.resources.timeline_graph_legend_instant
import flocondesktop.composeapp.generated.resources.timeline_graph_legend_rows
import flocondesktop.composeapp.generated.resources.timeline_graph_next_session
import flocondesktop.composeapp.generated.resources.timeline_graph_previous_session
import flocondesktop.composeapp.generated.resources.timeline_graph_session
import flocondesktop.composeapp.generated.resources.timeline_graph_slowest
import flocondesktop.composeapp.generated.resources.timeline_graph_summary
import flocondesktop.composeapp.generated.resources.timeline_graph_zoom_in
import flocondesktop.composeapp.generated.resources.timeline_graph_zoom_out
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineRowUiModel
import io.github.openflocon.flocondesktop.features.timeline.view.categoryColors
import io.github.openflocon.flocondesktop.features.timeline.view.label
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconButton
import org.jetbrains.compose.resources.stringResource
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

private const val ZoomStep = 1.25
private const val WheelZoomPerTick = 1.15
private const val PinchMinFactor = 0.5
private const val PinchMaxFactor = 2.0
private const val InstantLabelFlipRatio = 0.84f
private const val InstantLabelOffsetPx = 14f
private const val BarPaddingPx = 6f
private const val IconSizePx = 14f
private const val BarRadiusPx = 5f
private const val LabelFontPx = 12f
private const val ChipFontPx = 11f
private const val ChipHeightPx = 18f
private const val ChipPaddingPx = 6f

/** The previous and next session of the graph: [position] starts at 1. */
internal data class GraphSessionUiModel(val position: Int, val total: Int) {
    val hasPrevious: Boolean get() = position > 1
    val hasNext: Boolean get() = position < total
}

/**
 * The events of a session on a time axis, grouped by type in lanes. Everything is drawn in one [Canvas], only what is in
 * the viewport: wheel to scroll, ctrl or cmd + wheel to zoom, drag to move, click to select.
 */
@Composable
internal fun TimelineGraph(
    model: GraphUiModel,
    session: GraphSessionUiModel?,
    selectedKey: String?,
    onSelect: (TimelineRowUiModel) -> Unit,
    onPreviousSession: () -> Unit,
    onNextSession: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    val density = LocalDensity.current.density
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    // logical px: the canvas is drawn scaled by the density
    val widthPx = canvasSize.width / density
    val heightPx = canvasSize.height / density
    val trackWidth = max(widthPx - LaneLabelWidthPx - TrackEndMarginPx, 1f)
    val trackHeight = max(heightPx - AxisHeightPx, 0f)
    val contentHeight = model.contentHeightPx()

    // fitted: the whole session is shown and follows the new events, until the user zooms or moves
    var fitted by remember { mutableStateOf(true) }
    var viewport by remember { mutableStateOf(GraphViewport()) }
    var hovered by remember { mutableStateOf<GraphEventUiModel?>(null) }
    LaunchedEffect(fitted, model.sessionDurationMs, trackWidth) {
        if (fitted) viewport = fitViewport(model.sessionDurationMs, trackWidth).copy(scrollY = viewport.scrollY)
    }
    val maxScroll = max(contentHeight - trackHeight, 0f)
    val currentViewport = viewport.copy(scrollY = viewport.scrollY.coerceIn(0f, maxScroll))

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
    ) {
        GraphToolbar(
            model = model,
            session = session,
            onPreviousSession = onPreviousSession,
            onNextSession = onNextSession,
            onZoomIn = {
                fitted = false
                viewport = currentViewport.zoomed(ZoomStep, trackWidth / 2)
            },
            onZoomOut = {
                fitted = false
                viewport = currentViewport.zoomed(1 / ZoomStep, trackWidth / 2)
            },
            onFit = { fitted = true },
            onLatest = {
                fitted = false
                viewport = currentViewport.jumpedToLatest(model, trackWidth, trackHeight)
            },
        )

        val measurer = rememberTextMeasurer(cacheSize = 256)
        val imagePainter = rememberVectorPainter(Icons.Outlined.Image)
        val laneLabels = GraphLane.entries.associateWith { stringResource(it.source.label()) }
        val laneColors = GraphLane.entries.associateWith { it.source.categoryColors().first }
        val description = stringResource(Res.string.timeline_graph_description, model.eventCount)
        val palette = GraphPalette(
            axis = colors.bg200,
            panel = colors.bg100,
            line = colors.line,
            grid = colors.line.copy(alpha = 0.5f),
            ink = colors.ink,
            inkMuted = colors.inkMuted,
            onBar = colors.bg000,
            chip = colors.bg000,
            error = colors.statusError,
            selection = colors.ink,
        )

        Canvas(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .semantics { contentDescription = description }
                .pointerHoverIcon(if (hovered != null) PointerIcon.Hand else PointerIcon.Default)
                .onSizeChanged { canvasSize = it }
                .pointerInput(model, density, trackWidth, maxScroll) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            val change = event.changes.firstOrNull() ?: continue
                            val x = change.position.x / density - LaneLabelWidthPx
                            val y = change.position.y / density - AxisHeightPx
                            when (event.type) {
                                PointerEventType.Scroll -> {
                                    val delta = change.scrollDelta
                                    val zoom = event.keyboardModifiers.isCtrlPressed || event.keyboardModifiers.isMetaPressed
                                    fitted = false
                                    viewport = if (zoom) {
                                        val factor = if (delta.y < 0) WheelZoomPerTick else 1 / WheelZoomPerTick
                                        viewport.zoomed(factor, x.coerceIn(0f, trackWidth))
                                    } else if (event.keyboardModifiers.isShiftPressed || delta.x != 0f) {
                                        val ticks = if (delta.x != 0f) delta.x else delta.y
                                        viewport.panned(ticks * 40 / viewport.pxPerMs)
                                    } else {
                                        viewport.copy(scrollY = (viewport.scrollY + delta.y * 40).coerceIn(0f, maxScroll))
                                    }
                                    change.consume()
                                }

                                // the pinch of the trackpad: the factor of this step, around the fingers
                                PointerEventType.ScaleChange -> {
                                    val factor = change.scaleFactor.toDouble()
                                    if (factor > 0.0 && factor != 1.0) {
                                        fitted = false
                                        viewport = viewport.zoomed(factor.coerceIn(PinchMinFactor, PinchMaxFactor), x.coerceIn(0f, trackWidth))
                                        change.consume()
                                    }
                                }

                                PointerEventType.Move -> {
                                    hovered = if (x >= 0f && y >= 0f) {
                                        model.hitTest(viewport, x, y + viewport.scrollY)
                                    } else {
                                        null
                                    }
                                }

                                PointerEventType.Exit -> hovered = null
                            }
                        }
                    }
                }
                .pointerInput(model, density, maxScroll) {
                    detectTapGestures { offset ->
                        val x = offset.x / density - LaneLabelWidthPx
                        val y = offset.y / density - AxisHeightPx
                        if (x >= 0f && y >= 0f) {
                            model.hitTest(viewport, x, y + viewport.scrollY)?.let { onSelect(it.row) }
                        }
                    }
                }
                .pointerInput(model, density, maxScroll) {
                    detectDragGestures { change, drag ->
                        change.consume()
                        fitted = false
                        viewport = viewport
                            .panned(-drag.x / density / viewport.pxPerMs)
                            .copy(scrollY = (viewport.scrollY - drag.y / density).coerceIn(0f, maxScroll))
                    }
                },
        ) {
            scale(density, density, pivot = Offset.Zero) {
                drawGraph(
                    model = model,
                    viewport = currentViewport,
                    width = widthPx,
                    height = heightPx,
                    selectedKey = selectedKey,
                    palette = palette,
                    laneColors = laneColors,
                    laneLabels = laneLabels,
                    measurer = measurer,
                    imagePainter = imagePainter,
                )
            }
        }

        if (model.eventCount == 0) {
            Text(
                text = stringResource(Res.string.timeline_graph_empty),
                style = FloconTheme.typography.bodyMedium,
                color = colors.inkMuted,
            )
        }
        GraphLegend(hovered = hovered)
    }
}

private class GraphPalette(
    val axis: Color,
    val panel: Color,
    val line: Color,
    val grid: Color,
    val ink: Color,
    val inkMuted: Color,
    val onBar: Color,
    val chip: Color,
    val error: Color,
    val selection: Color,
)

@Composable
private fun GraphToolbar(
    model: GraphUiModel,
    session: GraphSessionUiModel?,
    onPreviousSession: () -> Unit,
    onNextSession: () -> Unit,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onFit: () -> Unit,
    onLatest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    val summary = model.toSummaryText()
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
    ) {
        Text(
            text = buildString {
                append(stringResource(Res.string.timeline_graph_summary, summary.visible, summary.sessionDuration, summary.failed))
                if (summary.slowestName != null && summary.slowestDuration != null) {
                    append(" - ")
                    append(stringResource(Res.string.timeline_graph_slowest, summary.slowestName, summary.slowestDuration))
                }
            },
            style = FloconTheme.typography.labelMedium.copy(fontFamily = FontFamily.Monospace),
            color = colors.inkMuted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        if (session != null && session.total > 1) {
            FloconButton(onClick = onPreviousSession) {
                Text(text = "‹")
            }
            Text(
                text = stringResource(Res.string.timeline_graph_session, session.position, session.total),
                style = FloconTheme.typography.labelMedium,
                color = colors.inkMuted,
                maxLines = 1,
            )
            FloconButton(onClick = onNextSession) {
                Text(text = "›")
            }
        }
        FloconButton(onClick = onZoomOut) { Text(stringResource(Res.string.timeline_graph_zoom_out)) }
        FloconButton(onClick = onZoomIn) { Text(stringResource(Res.string.timeline_graph_zoom_in)) }
        FloconButton(onClick = onLatest) { Text(stringResource(Res.string.timeline_graph_latest)) }
        FloconButton(onClick = onFit) { Text(stringResource(Res.string.timeline_graph_fit)) }
    }
}

@Composable
private fun GraphLegend(
    hovered: GraphEventUiModel?,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = FloconTheme.spacing.small)
            .height(FloconTheme.sizes.controlHeight / 2),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.large),
    ) {
        if (hovered != null) {
            // what is under the pointer, the bars can be too narrow to hold their text
            Text(
                text = hovered.tooltip(),
                style = FloconTheme.typography.labelMedium.copy(fontFamily = FontFamily.Monospace),
                color = colors.ink,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        } else {
            listOf(
                Res.string.timeline_graph_legend_bar,
                Res.string.timeline_graph_legend_instant,
                Res.string.timeline_graph_legend_failed,
                Res.string.timeline_graph_legend_rows,
            ).forEach {
                Text(
                    text = stringResource(it),
                    style = FloconTheme.typography.labelSmall,
                    color = colors.inkFaint,
                    maxLines = 1,
                )
            }
        }
    }
}

// "GET /v1/feed - +190 ms - 640 ms - 200"
internal fun GraphEventUiModel.tooltip(): String = buildList {
    add(listOfNotNull(method, name).joinToString(" "))
    add(formatGraphStart(startMs))
    add(if (isInstant) "instant" else formatGraphDuration(durationMs))
    statusCode?.let { add(it.toString()) }
}.joinToString(" - ")

private fun DrawScope.drawGraph(
    model: GraphUiModel,
    viewport: GraphViewport,
    width: Float,
    height: Float,
    selectedKey: String?,
    palette: GraphPalette,
    laneColors: Map<GraphLane, Color>,
    laneLabels: Map<GraphLane, String>,
    measurer: TextMeasurer,
    imagePainter: Painter,
) {
    // the text is measured in logical px: the canvas is already scaled
    val unit = Density(1f)
    val labelStyle = TextStyle(fontSize = LabelFontPx.sp, fontWeight = FontWeight.SemiBold)
    val chipStyle = TextStyle(fontSize = ChipFontPx.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Medium)
    val trackRight = width
    val viewEndMs = viewport.msAt(trackRight - LaneLabelWidthPx)
    val tops = model.laneTops()

    // the lanes
    clipRect(left = LaneLabelWidthPx, top = AxisHeightPx, right = width, bottom = height) {
        val step = axisStepMs(viewport.pxPerMs)
        var tick = ceil(viewport.startMs / step).toLong() * step
        while (tick <= viewEndMs) {
            val x = LaneLabelWidthPx + viewport.xOf(tick)
            drawLine(palette.grid, Offset(x, AxisHeightPx), Offset(x, height), strokeWidth = 1f)
            tick += step
        }
        model.lanes.forEachIndexed { laneIndex, lane ->
            val laneTop = AxisHeightPx + tops[laneIndex] - viewport.scrollY
            if (laneTop + lane.heightPx() < AxisHeightPx || laneTop > height) return@forEachIndexed
            val color = laneColors.getValue(lane.lane)
            lane.rows.forEachIndexed { rowIndex, row ->
                val rowTop = laneTop + rowIndex * RowHeightPx
                if (rowTop + RowHeightPx < AxisHeightPx || rowTop > height) return@forEachIndexed
                // the events of a row do not overlap: only the ones in the window are looked at
                val margin = max(DiamondSizePx, MinBarWidthPx) / viewport.pxPerMs
                var i = firstVisibleIndex(row, viewport.startMs - margin)
                while (i < row.size) {
                    val event = row[i]
                    if (event.startMs - margin > viewEndMs) break
                    val selected = event.key == selectedKey
                    val fill = if (event.isError) palette.error else color
                    if (event.isInstant) {
                        drawInstant(event, viewport, rowTop, fill, selected, palette, measurer, labelStyle, unit, width)
                    } else {
                        drawBar(event, viewport, rowTop, fill, selected, palette, measurer, labelStyle, chipStyle, unit, imagePainter, width)
                    }
                    i++
                }
            }
            if (laneIndex < model.lanes.lastIndex) {
                val y = laneTop + lane.heightPx()
                drawLine(palette.line, Offset(LaneLabelWidthPx, y), Offset(width, y), strokeWidth = LaneSeparatorPx)
            }
        }
    }

    // the axis, over the lanes
    drawRect(palette.axis, Offset.Zero, Size(width, AxisHeightPx))
    drawLine(palette.line, Offset(0f, AxisHeightPx), Offset(width, AxisHeightPx), strokeWidth = 1f)
    clipRect(left = LaneLabelWidthPx, top = 0f, right = width, bottom = AxisHeightPx) {
        val step = axisStepMs(viewport.pxPerMs)
        var tick = ceil(viewport.startMs / step).toLong() * step
        while (tick <= viewEndMs) {
            val x = LaneLabelWidthPx + viewport.xOf(tick)
            val text = measurer.measure(
                formatAxisLabel(tick, step),
                TextStyle(fontSize = LabelFontPx.sp, fontFamily = FontFamily.Monospace, color = palette.inkMuted),
                density = unit,
                maxLines = 1,
            )
            drawText(text, topLeft = Offset(x + 4f, (AxisHeightPx - text.size.height) / 2f))
            tick += step
        }
    }

    // the column of the lanes, over everything
    drawRect(palette.panel, Offset(0f, AxisHeightPx), Size(LaneLabelWidthPx, height - AxisHeightPx))
    drawLine(palette.line, Offset(LaneLabelWidthPx, 0f), Offset(LaneLabelWidthPx, height), strokeWidth = 1f)
    clipRect(left = 0f, top = AxisHeightPx, right = LaneLabelWidthPx, bottom = height) {
        model.lanes.forEachIndexed { laneIndex, lane ->
            val laneTop = AxisHeightPx + tops[laneIndex] - viewport.scrollY
            if (laneTop + lane.heightPx() < AxisHeightPx || laneTop > height) return@forEachIndexed
            val text = measurer.measure(
                laneLabels.getValue(lane.lane),
                labelStyle.copy(color = palette.ink),
                density = unit,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                constraints = Constraints(maxWidth = (LaneLabelWidthPx - 40f).toInt()),
            )
            val y = laneTop + (RowHeightPx - text.size.height) / 2f
            drawCircle(laneColors.getValue(lane.lane), radius = 3f, center = Offset(22f, y + text.size.height / 2f))
            drawText(text, topLeft = Offset(32f, y))
            if (laneIndex < model.lanes.lastIndex) {
                val line = laneTop + lane.heightPx()
                drawLine(palette.line, Offset(0f, line), Offset(LaneLabelWidthPx, line), strokeWidth = LaneSeparatorPx)
            }
        }
    }
}

private fun DrawScope.drawBar(
    event: GraphEventUiModel,
    viewport: GraphViewport,
    rowTop: Float,
    fill: Color,
    selected: Boolean,
    palette: GraphPalette,
    measurer: TextMeasurer,
    labelStyle: TextStyle,
    chipStyle: TextStyle,
    unit: Density,
    imagePainter: Painter,
    canvasWidth: Float,
) {
    val x = LaneLabelWidthPx + viewport.xOf(event.startMs)
    val barWidth = max(MinBarWidthPx, (event.durationMs * viewport.pxPerMs).toFloat())
    val top = rowTop + BarTopPx
    drawRoundRect(fill, Offset(x, top), Size(barWidth, BarHeightPx), CornerRadius(BarRadiusPx))
    if (selected) {
        drawRoundRect(palette.selection, Offset(x - 1f, top - 1f), Size(barWidth + 2f, BarHeightPx + 2f), CornerRadius(BarRadiusPx), style = Stroke(2f))
    }

    // the content is placed in the visible part of the bar
    val visibleLeft = max(x, LaneLabelWidthPx)
    val visibleRight = min(x + barWidth, canvasWidth)
    val visibleWidth = visibleRight - visibleLeft
    if (visibleWidth <= 0f) return
    var cursor = visibleLeft + BarPaddingPx
    val centerY = top + BarHeightPx / 2f
    if (event.isImage && showBarIcon(barWidth)) {
        translate(cursor, centerY - IconSizePx / 2f) {
            with(imagePainter) { draw(Size(IconSizePx, IconSizePx), colorFilter = tint(palette.onBar)) }
        }
        cursor += IconSizePx + BarPaddingPx
    }
    if (event.method != null && showBarChip(barWidth)) {
        val method = measurer.measure(event.method, chipStyle.copy(color = palette.ink), density = unit, maxLines = 1)
        val chipWidth = method.size.width + 2 * ChipPaddingPx
        drawRoundRect(palette.chip, Offset(cursor, centerY - ChipHeightPx / 2f), Size(chipWidth, ChipHeightPx), CornerRadius(4f))
        drawText(method, topLeft = Offset(cursor + ChipPaddingPx, centerY - method.size.height / 2f))
        cursor += chipWidth + BarPaddingPx
    }
    val remaining = visibleRight - BarPaddingPx - cursor
    if (!showBarText(remaining)) return
    val duration = formatGraphDuration(event.durationMs)
    val full = measurer.measure("${event.name}  -  $duration", labelStyle.copy(color = palette.onBar), density = unit, maxLines = 1)
    // the name and the duration when both fit, else the name alone
    val text = if (full.size.width <= remaining) {
        full
    } else {
        measurer.measure(
            event.name,
            labelStyle.copy(color = palette.onBar),
            density = unit,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            constraints = Constraints(maxWidth = remaining.toInt()),
        )
    }
    drawText(text, topLeft = Offset(cursor, centerY - text.size.height / 2f))
}

private fun DrawScope.drawInstant(
    event: GraphEventUiModel,
    viewport: GraphViewport,
    rowTop: Float,
    fill: Color,
    selected: Boolean,
    palette: GraphPalette,
    measurer: TextMeasurer,
    labelStyle: TextStyle,
    unit: Density,
    canvasWidth: Float,
) {
    val x = LaneLabelWidthPx + viewport.xOf(event.startMs)
    val centerY = rowTop + RowHeightPx / 2f
    val half = DiamondSizePx / 2f + 1f
    val diamond = Path().apply {
        moveTo(x, centerY - half)
        lineTo(x + half, centerY)
        lineTo(x, centerY + half)
        lineTo(x - half, centerY)
        close()
    }
    drawPath(diamond, fill)
    if (selected) drawPath(diamond, palette.selection, style = Stroke(2f))

    val text = measurer.measure(event.name, labelStyle.copy(color = palette.ink, fontWeight = FontWeight.Normal), density = unit, maxLines = 1)
    // past the right of the track, the label goes to the left of the diamond
    val trackWidth = canvasWidth - LaneLabelWidthPx - TrackEndMarginPx
    val flip = x - LaneLabelWidthPx > trackWidth * InstantLabelFlipRatio
    val labelX = if (flip) x - InstantLabelOffsetPx - text.size.width else x + InstantLabelOffsetPx
    drawText(text, topLeft = Offset(labelX, centerY - text.size.height / 2f))
}

@Composable
private fun TimelineGraphPreviewContent(isDarkTheme: Boolean) {
    FloconTheme(isDarkTheme = isDarkTheme) {
        val model = remember { buildGraphUiModel(previewGraphEvents(), PreviewSessionDurationMs) }
        TimelineGraph(
            model = model,
            session = GraphSessionUiModel(position = 2, total = 3),
            selectedKey = model.lanes.flatMap { it.rows.flatten() }.first { it.row.id == "12" }.key,
            onSelect = {},
            onPreviousSession = {},
            onNextSession = {},
            modifier = Modifier.height(PreviewHeight),
        )
    }
}

private val PreviewHeight = 480.dp

@Preview
@Composable
private fun TimelineGraphDarkPreview() = TimelineGraphPreviewContent(isDarkTheme = true)

@Preview
@Composable
private fun TimelineGraphLightPreview() = TimelineGraphPreviewContent(isDarkTheme = false)
