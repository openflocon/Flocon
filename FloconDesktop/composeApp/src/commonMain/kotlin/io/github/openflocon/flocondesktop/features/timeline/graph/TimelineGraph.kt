package io.github.openflocon.flocondesktop.features.timeline.graph

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
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
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
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
import flocondesktop.composeapp.generated.resources.timeline_graph_following
import flocondesktop.composeapp.generated.resources.timeline_graph_latest
import flocondesktop.composeapp.generated.resources.timeline_graph_legend_app_start
import flocondesktop.composeapp.generated.resources.timeline_graph_legend_bar
import flocondesktop.composeapp.generated.resources.timeline_graph_legend_failed
import flocondesktop.composeapp.generated.resources.timeline_graph_legend_instant
import flocondesktop.composeapp.generated.resources.timeline_graph_legend_rows
import flocondesktop.composeapp.generated.resources.timeline_graph_next_session
import flocondesktop.composeapp.generated.resources.timeline_graph_paused
import flocondesktop.composeapp.generated.resources.timeline_graph_previous_session
import flocondesktop.composeapp.generated.resources.timeline_graph_session
import flocondesktop.composeapp.generated.resources.timeline_graph_slowest
import flocondesktop.composeapp.generated.resources.timeline_graph_summary
import flocondesktop.composeapp.generated.resources.timeline_graph_zoom_in
import flocondesktop.composeapp.generated.resources.timeline_graph_zoom_out
import flocondesktop.composeapp.generated.resources.timeline_graph_zoom_unit
import io.github.openflocon.domain.common.time.formatTimestamp
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.flocondesktop.features.images.view.viewer.PinchZoomEffect
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineRowUiModel
import io.github.openflocon.flocondesktop.features.timeline.view.categoryColors
import io.github.openflocon.flocondesktop.features.timeline.view.label
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconButton
import io.github.openflocon.library.designsystem.components.FloconTextField
import kotlinx.coroutines.flow.first
import org.jetbrains.compose.resources.stringResource
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

private const val ZoomStep = 1.25
private const val WheelZoomPerTick = 1.15
private const val ZoomBoxFillAlpha = 0.12f
private const val ZoomFieldMaxDigits = 6
private const val PinchMinFactor = 0.5
private const val PinchMaxFactor = 2.0
private const val InstantLabelFlipRatio = 0.84f
private const val BarPaddingPx = 6f
private const val IconSizePx = 14f
private const val BarRadiusPx = 5f
private const val LabelFontPx = 12f
private const val ChipFontPx = 11f
private const val ChipHeightPx = 18f
private const val ChipPaddingPx = 6f
private const val InstantLabelPaddingPx = 4f
private const val AppStartLineWidthPx = 2f
private const val ClockFontPx = 10f
private const val AxisTextBottomPx = 5f
private const val PopoverWidthPx = 214f

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
    // true while the zoom field is being edited: the keys of the screen must leave it alone
    onEditingChange: (Boolean) -> Unit = {},
    // keeps the most recent event in view, until the user moves the graph. The option of the list, shared
    autoScroll: Boolean = false,
    // the screenshots strip above the axis: [screenshotsEnabled] is the setting, the captures are in [model]
    screenshotsEnabled: Boolean = false,
    // false for a device that cannot take them (an Android over wifi): no strip, nothing to turn on
    screenshotsAvailable: Boolean = true,
    onTurnOnScreenshots: () -> Unit = {},
) {
    val colors = FloconTheme.colors
    val density = LocalDensity.current.density
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    // logical px: the canvas is drawn scaled by the density
    val widthPx = canvasSize.width / density
    val heightPx = canvasSize.height / density
    val trackWidth = trackWidthOf(widthPx)
    val trackHeight = max(heightPx - AxisHeightPx, 0f)
    val contentHeight = model.contentHeightPx()

    // fitted: the whole session is shown and follows the new events, until the user zooms or moves. Opens at 100 %
    var fitted by remember { mutableStateOf(false) }
    // the auto scroll waits while the user looks around: moved by hand, until latest or fit
    var paused by remember { mutableStateOf(false) }
    var opened by remember { mutableStateOf(false) }
    var viewport by remember { mutableStateOf(GraphViewport()) }
    var hovered by remember { mutableStateOf<GraphEventUiModel?>(null) }
    // the screenshot of the strip under the pointer, shown larger
    var hoveredScreenshot by remember { mutableStateOf<GraphScreenshotUiModel?>(null) }
    // the box drawn with the pointer to zoom on it, in px of the canvas: start and current point
    var zoomBox by remember { mutableStateOf<Pair<Offset, Offset>?>(null) }
    // the last x of the pointer in the track: the pinch zooms around it
    var pointerX by remember { mutableStateOf(0f) }
    // shift + drag moves the graph instead of drawing a box
    var shiftHeld by remember { mutableStateOf(false) }
    // opens at 100 % once the canvas has its real width, read when it is known and not from an earlier layout
    LaunchedEffect(Unit) {
        val width = snapshotFlow { canvasSize.width }.first { it > 0 }
        if (!opened) {
            viewport = defaultViewport(trackWidthOf(width / density))
            opened = true
        }
    }
    // a more recent event than the ones in view: the graph follows, but not on an older session
    val followsLatest = session == null || !session.hasNext
    // a new screenshot can be the most recent thing of the graph, with no new event
    val latestMs = remember(model) { model.latestMs() }
    // read by the pointer handlers, which are not restarted for a screenshot: they must see the latest graph
    val currentModel by rememberUpdatedState(model)
    val currentFollowsLatest by rememberUpdatedState(followsLatest)
    LaunchedEffect(model.sessionDurationMs, latestMs, autoScroll) {
        if (autoScroll && !paused && !fitted && opened && followsLatest) {
            viewport = viewport.followedLatest(model, trackWidth)
        }
    }
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
            zoomPercent = currentViewport.zoomPercent(trackWidth),
            onEditingChange = onEditingChange,
            onZoomPercent = {
                fitted = false
                viewport = currentViewport.zoomedToPercent(it, trackWidth)
            },
            onZoomIn = {
                fitted = false
                viewport = currentViewport.zoomed(ZoomStep, trackWidth / 2)
            },
            onZoomOut = {
                fitted = false
                viewport = currentViewport.zoomed(1 / ZoomStep, trackWidth / 2)
            },
            following = if (autoScroll) !paused else null,
            onFit = {
                fitted = true
                paused = false
            },
            onLatest = {
                fitted = false
                paused = false
                viewport = currentViewport.jumpedToLatest(model, trackWidth, trackHeight)
            },
        )

        // the trackpad pinch (macOS), Compose does not deliver it
        PinchZoomEffect { factor ->
            fitted = false
            viewport = viewport.zoomed(factor.toDouble().coerceIn(PinchMinFactor, PinchMaxFactor), pointerX.coerceIn(0f, trackWidth))
        }

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
            inkFaint = colors.inkFaint,
            onBar = colors.bg000,
            chip = colors.bg000,
            error = colors.statusError,
            appStart = TimelineSource.AppStart.categoryColors().first,
            selection = colors.ink,
        )

        if (screenshotsAvailable) {
            ScreenshotStrip(
                screenshots = model.screenshots,
                viewport = currentViewport,
                trackWidthPx = trackWidth,
                enabled = screenshotsEnabled,
                hoveredTimeMs = hoveredScreenshot?.timeMs,
                onHover = { hoveredScreenshot = it },
                onTurnOn = onTurnOnScreenshots,
            )
        }

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .semantics { contentDescription = description }
                    .pointerHoverIcon(if (hovered != null) PointerIcon.Hand else PointerIcon.Default)
                    .onSizeChanged { canvasSize = it }
                    .pointerInput(model.lanes, density, trackWidth, maxScroll) {
                        awaitPointerEventScope {
                            while (true) {
                                val event = awaitPointerEvent()
                                val change = event.changes.firstOrNull() ?: continue
                                shiftHeld = event.keyboardModifiers.isShiftPressed
                                val x = change.position.x / density - LaneLabelWidthPx
                                pointerX = x
                                val y = change.position.y / density - AxisHeightPx
                                when (event.type) {
                                    PointerEventType.Scroll -> {
                                        val delta = change.scrollDelta
                                        val zoom = event.keyboardModifiers.isCtrlPressed || event.keyboardModifiers.isMetaPressed
                                        fitted = false
                                        viewport = if (zoom) {
                                            val factor = if (delta.y < 0) WheelZoomPerTick else 1 / WheelZoomPerTick
                                            viewport.zoomed(factor, x.coerceIn(0f, trackWidth))
                                        } else if (event.keyboardModifiers.isShiftPressed || abs(delta.x) > abs(delta.y)) {
                                            // a real move along the time: the trackpad also sends a small horizontal drift
                                            paused = true
                                            val ticks = if (delta.x != 0f) delta.x else delta.y
                                            viewport.panned(ticks * 40 / viewport.pxPerMs, currentModel.panLimitMs(currentFollowsLatest, System.currentTimeMillis()), trackWidth)
                                        } else {
                                            viewport.copy(scrollY = (viewport.scrollY + delta.y * 40).coerceIn(0f, maxScroll))
                                        }
                                        change.consume()
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
                    .pointerInput(model.lanes, density, maxScroll) {
                        detectTapGestures { offset ->
                            val x = offset.x / density - LaneLabelWidthPx
                            val y = offset.y / density - AxisHeightPx
                            if (x >= 0f && y >= 0f) {
                                model.hitTest(viewport, x, y + viewport.scrollY)?.let { onSelect(it.row) }
                            }
                        }
                    }
                    .pointerInput(model.lanes, density, trackWidth, maxScroll) {
                        detectDragGestures(
                            onDragStart = { offset -> if (!shiftHeld) zoomBox = offset to offset },
                            onDragEnd = {
                                zoomBox?.let { (start, end) ->
                                    fitted = false
                                    paused = true
                                    viewport = viewport.zoomedToRange(start.x / density - LaneLabelWidthPx, end.x / density - LaneLabelWidthPx, trackWidth)
                                }
                                zoomBox = null
                            },
                            onDragCancel = { zoomBox = null },
                        ) { change, drag ->
                            change.consume()
                            val box = zoomBox
                            if (box != null) {
                                zoomBox = box.first to change.position
                            } else {
                                fitted = false
                                paused = true
                                viewport = viewport
                                    .panned(-drag.x / density / viewport.pxPerMs, currentModel.panLimitMs(currentFollowsLatest, System.currentTimeMillis()), trackWidth)
                                    .copy(scrollY = (viewport.scrollY - drag.y / density).coerceIn(0f, maxScroll))
                            }
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
                        hoveredKey = hovered?.key,
                        palette = palette,
                        laneColors = laneColors,
                        laneLabels = laneLabels,
                        measurer = measurer,
                        imagePainter = imagePainter,
                    )
                    zoomBox?.let { (start, end) ->
                        // the box covers the lanes only, not the axis and the names of the lanes
                        val left = max(min(start.x, end.x) / density, LaneLabelWidthPx)
                        val right = max(max(start.x, end.x) / density, LaneLabelWidthPx)
                        val top = min(start.y, end.y) / density
                        val bottom = max(start.y, end.y) / density
                        drawRect(palette.selection.copy(alpha = ZoomBoxFillAlpha), Offset(left, top), Size(right - left, bottom - top))
                        drawRect(palette.selection, Offset(left, top), Size(right - left, bottom - top), style = Stroke(1f))
                    }
                }
            }

            // the hovered thumbnail, larger, under its place in the strip. A screenshot that left the strip (cap, new session) is not shown
            hoveredScreenshot?.takeIf { it in model.screenshots }?.let { shot ->
                val centerX = LaneLabelWidthPx + currentViewport.xOf(shot.timeMs)
                ScreenshotPopover(
                    screenshot = shot,
                    modifier = Modifier.offset(
                        x = (centerX - PopoverWidthPx / 2).coerceIn(0f, max(widthPx - PopoverWidthPx, 0f)).dp,
                        y = 0.dp,
                    ),
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

// the width of the track in a canvas of [canvasWidthPx]
private fun trackWidthOf(canvasWidthPx: Float) = max(canvasWidthPx - LaneLabelWidthPx - TrackEndMarginPx, 1f)

private class GraphPalette(
    val axis: Color,
    val panel: Color,
    val line: Color,
    val grid: Color,
    val ink: Color,
    val inkMuted: Color,
    val inkFaint: Color,
    val onBar: Color,
    val chip: Color,
    val error: Color,
    val appStart: Color,
    val selection: Color,
)

@Composable
private fun GraphToolbar(
    model: GraphUiModel,
    session: GraphSessionUiModel?,
    onPreviousSession: () -> Unit,
    onNextSession: () -> Unit,
    zoomPercent: Int,
    onZoomPercent: (Int) -> Unit,
    onEditingChange: (Boolean) -> Unit,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onFit: () -> Unit,
    onLatest: () -> Unit,
    // null: no auto scroll. Else true while the graph follows the new events, false once the user moved it
    following: Boolean?,
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
        GraphZoomField(percent = zoomPercent, onPercentChange = onZoomPercent, onEditingChange = onEditingChange)
        FloconButton(onClick = onZoomIn) { Text(stringResource(Res.string.timeline_graph_zoom_in)) }
        if (following != null) {
            Text(
                text = stringResource(if (following) Res.string.timeline_graph_following else Res.string.timeline_graph_paused),
                style = FloconTheme.typography.labelMedium,
                color = if (following) colors.statusSuccess else colors.inkFaint,
                maxLines = 1,
            )
        }
        FloconButton(onClick = onLatest) { Text(stringResource(Res.string.timeline_graph_latest)) }
        FloconButton(onClick = onFit) { Text(stringResource(Res.string.timeline_graph_fit)) }
    }
}

// the zoom in percent, typed and validated with enter or when the focus leaves the field
@Composable
private fun GraphZoomField(
    percent: Int,
    onPercentChange: (Int) -> Unit,
    onEditingChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    var text by remember(percent) { mutableStateOf(percent.toString()) }
    val apply = { text.toIntOrNull()?.takeIf { it > 0 && it != percent }?.let(onPercentChange) ?: Unit }
    FloconTextField(
        value = text,
        onValueChange = { value -> text = value.filter { it.isDigit() }.take(ZoomFieldMaxDigits) },
        suffix = { Text(stringResource(Res.string.timeline_graph_zoom_unit)) },
        textStyle = FloconTheme.typography.labelMedium.copy(fontFamily = FontFamily.Monospace),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { apply() }),
        modifier = modifier
            .width(FloconTheme.sizes.columnMedium)
            .height(FloconTheme.sizes.controlHeight)
            .onFocusChanged {
                onEditingChange(it.isFocused)
                if (!it.isFocused) apply()
            }
            .onPreviewKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown && (event.key == Key.Enter || event.key == Key.NumPadEnter)) {
                    apply()
                    true
                } else {
                    false
                }
            },
    )
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
                Res.string.timeline_graph_legend_app_start,
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
    hoveredKey: String?,
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
        // no tick before the start of the session
        var tick = max(ceil(viewport.startMs / step).toLong() * step, 0L)
        while (tick <= viewEndMs) {
            val x = LaneLabelWidthPx + viewport.xOf(tick)
            drawLine(palette.grid, Offset(x, AxisHeightPx), Offset(x, height), strokeWidth = 1f)
            tick += step
        }
        var deferredLabel: (() -> Unit)? = null
        // the start of the app, over all the lanes, under the events
        if (model.hasAppStart) {
            val x = LaneLabelWidthPx + viewport.xOf(0L)
            drawLine(palette.appStart, Offset(x, AxisHeightPx), Offset(x, height), strokeWidth = AppStartLineWidthPx)
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
                        val hoveredInstant = event.key == hoveredKey
                        drawInstant(
                            event, viewport, rowTop, fill, selected, palette, measurer, labelStyle, unit, width,
                            previous = row.getOrNull(i - 1),
                            next = row.getOrNull(i + 1),
                            withLabel = !hoveredInstant,
                        )
                        // the whole label of the hovered one, over the others, on a plain background
                        if (hoveredInstant) {
                            deferredLabel = { drawInstantFullLabel(event, viewport, rowTop, fill, palette, measurer, labelStyle, unit, width) }
                        }
                    } else {
                        drawBar(event, viewport, rowTop, fill, selected, palette, measurer, labelStyle, chipStyle, unit, imagePainter, width)
                        // a bar too short for its content opens on hover, over the others
                        if (event.key == hoveredKey) {
                            deferredLabel = { drawBarFull(event, viewport, rowTop, fill, selected, palette, measurer, labelStyle, chipStyle, unit, imagePainter, width) }
                        }
                    }
                    i++
                }
            }
            if (laneIndex < model.lanes.lastIndex) {
                val y = laneTop + lane.heightPx()
                drawLine(palette.line, Offset(LaneLabelWidthPx, y), Offset(width, y), strokeWidth = LaneSeparatorPx)
            }
        }
        deferredLabel?.invoke()
    }

    // the axis, over the lanes
    drawRect(palette.axis, Offset.Zero, Size(width, AxisHeightPx))
    drawLine(palette.line, Offset(0f, AxisHeightPx), Offset(width, AxisHeightPx), strokeWidth = 1f)
    clipRect(left = LaneLabelWidthPx, top = 0f, right = width, bottom = AxisHeightPx) {
        val step = axisStepMs(viewport.pxPerMs)
        // no tick before the start of the session
        var tick = max(ceil(viewport.startMs / step).toLong() * step, 0L)
        while (tick <= viewEndMs) {
            val x = LaneLabelWidthPx + viewport.xOf(tick)
            val text = measurer.measure(
                formatAxisLabel(tick, step),
                TextStyle(fontSize = LabelFontPx.sp, fontFamily = FontFamily.Monospace, color = palette.inkMuted),
                density = unit,
                maxLines = 1,
            )
            // the time of day above the duration, on some of the ticks: 5 s alone does not tell when it was
            val showClock = (tick / step) % CLOCK_EVERY_TICKS == 0L
            // the durations are all on the same line, at the bottom
            val textTop = AxisHeightPx - text.size.height - AxisTextBottomPx
            drawText(text, topLeft = Offset(x + 4f, textTop))
            if (showClock) {
                val clock = measurer.measure(
                    formatClockLabel(formatTimestamp(model.sessionStartTimestamp + tick), step),
                    TextStyle(fontSize = ClockFontPx.sp, fontFamily = FontFamily.Monospace, color = palette.inkFaint),
                    density = unit,
                    maxLines = 1,
                )
                drawText(clock, topLeft = Offset(x + 4f, textTop - clock.size.height))
            }
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

// the bar of a hovered event with all its content, longer than the bar when this one is too short for it
private fun DrawScope.drawBarFull(
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
    val method = event.method?.let { measurer.measure(it, chipStyle.copy(color = palette.ink), density = unit, maxLines = 1) }
    val text = measurer.measure(
        "${event.name}  -  ${formatGraphDuration(event.durationMs)}",
        labelStyle.copy(color = palette.onBar),
        density = unit,
        maxLines = 1,
    )
    val chipWidth = method?.let { it.size.width + 2 * ChipPaddingPx } ?: 0f
    val needed = BarPaddingPx +
        (if (event.isImage) IconSizePx + BarPaddingPx else 0f) +
        (if (method != null) chipWidth + BarPaddingPx else 0f) +
        text.size.width + BarPaddingPx
    val x = LaneLabelWidthPx + viewport.xOf(event.startMs)
    val barWidth = max(MinBarWidthPx, (event.durationMs * viewport.pxPerMs).toFloat())
    if (needed <= barWidth) return

    // from the start of the bar, moved to the left when it would go past the right of the canvas
    val left = min(max(x, LaneLabelWidthPx), canvasWidth - needed).coerceAtLeast(LaneLabelWidthPx)
    val top = rowTop + BarTopPx
    drawRoundRect(fill, Offset(left, top), Size(needed, BarHeightPx), CornerRadius(BarRadiusPx))
    if (selected) {
        drawRoundRect(palette.selection, Offset(left - 1f, top - 1f), Size(needed + 2f, BarHeightPx + 2f), CornerRadius(BarRadiusPx), style = Stroke(2f))
    }
    var cursor = left + BarPaddingPx
    val centerY = top + BarHeightPx / 2f
    if (event.isImage) {
        translate(cursor, centerY - IconSizePx / 2f) {
            with(imagePainter) { draw(Size(IconSizePx, IconSizePx), colorFilter = tint(palette.onBar)) }
        }
        cursor += IconSizePx + BarPaddingPx
    }
    if (method != null) {
        drawRoundRect(palette.chip, Offset(cursor, centerY - ChipHeightPx / 2f), Size(chipWidth, ChipHeightPx), CornerRadius(4f))
        drawText(method, topLeft = Offset(cursor + ChipPaddingPx, centerY - method.size.height / 2f))
        cursor += chipWidth + BarPaddingPx
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
    previous: GraphEventUiModel?,
    next: GraphEventUiModel?,
    withLabel: Boolean,
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
    if (!withLabel) return

    // the label stops before the neighbour: cut with an ellipsis, or not written when there is no room
    val onTheLeft = instantLabelOnTheLeft(x, canvasWidth)
    val room = instantLabelRoomPx(event, if (onTheLeft) previous else next, viewport.pxPerMs, onTheLeft)
    if (room != null && !showBarText(room)) return
    val text = measurer.measure(
        event.name,
        instantLabelStyle(labelStyle, palette),
        density = unit,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        constraints = if (room == null) Constraints() else Constraints(maxWidth = room.toInt().coerceAtLeast(0)),
    )
    drawText(text, topLeft = Offset(instantLabelX(x, text.size.width, onTheLeft), centerY - text.size.height / 2f))
}

// the whole label of an instant, on the color of its diamond: it hides what is under it
private fun DrawScope.drawInstantFullLabel(
    event: GraphEventUiModel,
    viewport: GraphViewport,
    rowTop: Float,
    fill: Color,
    palette: GraphPalette,
    measurer: TextMeasurer,
    labelStyle: TextStyle,
    unit: Density,
    canvasWidth: Float,
) {
    val x = LaneLabelWidthPx + viewport.xOf(event.startMs)
    val centerY = rowTop + RowHeightPx / 2f
    val onTheLeft = instantLabelOnTheLeft(x, canvasWidth)
    val text = measurer.measure(event.name, labelStyle.copy(color = palette.ink, fontWeight = FontWeight.Normal), density = unit, maxLines = 1)
    val left = instantLabelX(x, text.size.width, onTheLeft)
    val top = centerY - text.size.height / 2f
    // the colors of the lanes are opaque: nothing of the labels under it shows through
    drawRoundRect(
        fill,
        Offset(left - InstantLabelPaddingPx, top - 2f),
        Size(text.size.width + 2 * InstantLabelPaddingPx, text.size.height + 4f),
        CornerRadius(BarRadiusPx),
    )
    drawText(text, topLeft = Offset(left, top))
}

private fun instantLabelStyle(labelStyle: TextStyle, palette: GraphPalette) = labelStyle.copy(color = palette.ink, fontWeight = FontWeight.Normal)

// past the right of the track, the label goes to the left of the diamond
private fun DrawScope.instantLabelOnTheLeft(x: Float, canvasWidth: Float): Boolean {
    val trackWidth = canvasWidth - LaneLabelWidthPx - TrackEndMarginPx
    return x - LaneLabelWidthPx > trackWidth * InstantLabelFlipRatio
}

private fun instantLabelX(x: Float, textWidth: Int, onTheLeft: Boolean) = if (onTheLeft) x - InstantLabelOffsetPx - textWidth else x + InstantLabelOffsetPx

@Composable
private fun TimelineGraphPreviewContent(isDarkTheme: Boolean) {
    FloconTheme(isDarkTheme = isDarkTheme) {
        val model = remember { buildGraphUiModel(previewGraphEvents(), PreviewSessionDurationMs, hasAppStart = true) }
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
