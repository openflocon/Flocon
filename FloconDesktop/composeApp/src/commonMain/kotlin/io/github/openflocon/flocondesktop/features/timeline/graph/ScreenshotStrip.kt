package io.github.openflocon.flocondesktop.features.timeline.graph

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.timeline_screenshot_at
import flocondesktop.composeapp.generated.resources.timeline_screenshots
import flocondesktop.composeapp.generated.resources.timeline_screenshots_off_hint
import flocondesktop.composeapp.generated.resources.timeline_screenshots_turn_on
import flocondesktop.composeapp.generated.resources.timeline_screenshots_waiting
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconButton
import org.jetbrains.compose.resources.stringResource
import java.io.File

private val ThumbWidth = 58.dp
private val ThumbHeight = 122.dp
private val ThumbGap = 6.dp
private val PopoverImageWidth = 190.dp
private val PopoverImageHeight = 400.dp
private val HintHeight = 48.dp
private val DashLength = 6.dp

/**
 * The screenshots of the app above the time axis, at the time they were taken, on the same scale as the lanes.
 * [cursorTimeMs]: the time under the pointer in the graph, it highlights the screenshot the popover shows.
 */
@Composable
internal fun ScreenshotStrip(
    screenshots: List<GraphScreenshotUiModel>,
    viewport: GraphViewport,
    trackWidthPx: Float,
    enabled: Boolean,
    hoveredTimeMs: Long?,
    onHover: (GraphScreenshotUiModel?) -> Unit,
    onTurnOn: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
    ) {
        // the same column as the names of the lanes
        Box(modifier = Modifier.width(LaneLabelWidthPx.dp).padding(end = FloconTheme.spacing.small)) {
            Text(
                text = stringResource(Res.string.timeline_screenshots),
                style = FloconTheme.typography.labelMedium,
                color = colors.inkMuted,
                maxLines = 1,
            )
        }
        Box(modifier = Modifier.width(trackWidthPx.dp)) {
            when {
                screenshots.isEmpty() && !enabled -> OffHint(onTurnOn = onTurnOn)

                screenshots.isEmpty() -> Box(modifier = Modifier.height(HintHeight), contentAlignment = Alignment.CenterStart) {
                    Text(
                        text = stringResource(Res.string.timeline_screenshots_waiting),
                        style = FloconTheme.typography.bodySmall,
                        color = colors.inkMuted,
                    )
                }

                else -> Thumbnails(
                    screenshots = screenshots,
                    viewport = viewport,
                    trackWidthPx = trackWidthPx,
                    hoveredTimeMs = hoveredTimeMs,
                    onHover = onHover,
                )
            }
        }
    }
}

@Composable
private fun Thumbnails(
    screenshots: List<GraphScreenshotUiModel>,
    viewport: GraphViewport,
    trackWidthPx: Float,
    hoveredTimeMs: Long?,
    onHover: (GraphScreenshotUiModel?) -> Unit,
) {
    val colors = FloconTheme.colors
    val thumbWidthPx = ThumbWidth.value
    val visible = remember(screenshots, viewport.startMs, viewport.pxPerMs, trackWidthPx) {
        thinScreenshots(screenshots, viewport, minGapPx = thumbWidthPx + ThumbGap.value)
            .filter { viewport.xOf(it.timeMs) in -thumbWidthPx..(trackWidthPx + thumbWidthPx) }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(ThumbHeight)
            .clipToBounds(),
    ) {
        visible.forEach { shot ->
            val isHighlighted = hoveredTimeMs == shot.timeMs
            Box(
                modifier = Modifier
                    .offset(x = (viewport.xOf(shot.timeMs) - thumbWidthPx / 2).dp)
                    .size(width = ThumbWidth, height = ThumbHeight)
                    // hovering a thumbnail shows it larger, over the graph
                    .pointerInput(shot) {
                        awaitPointerEventScope {
                            while (true) {
                                when (awaitPointerEvent().type) {
                                    PointerEventType.Enter -> onHover(shot)
                                    PointerEventType.Exit -> onHover(null)
                                }
                            }
                        }
                    }
                    .clip(FloconTheme.shapes.small)
                    .background(colors.bg200)
                    .border(if (isHighlighted) 2.dp else 1.dp, if (isHighlighted) colors.accent else colors.line, FloconTheme.shapes.small),
            ) {
                // a file that is gone (cap, temporary folder cleaned) loads nothing: the empty box stays
                AsyncImage(
                    model = File(shot.filePath),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxHeight().fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun OffHint(onTurnOn: () -> Unit) {
    val colors = FloconTheme.colors
    val dash = DashLength.value
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(HintHeight)
            .drawBehind {
                drawRoundRect(
                    color = colors.line,
                    size = Size(size.width, size.height),
                    cornerRadius = CornerRadius(8.dp.toPx()),
                    style = Stroke(width = 1.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(dash.dp.toPx(), dash.dp.toPx()))),
                )
            }
            .padding(horizontal = FloconTheme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.medium),
    ) {
        Text(
            text = stringResource(Res.string.timeline_screenshots_off_hint),
            style = FloconTheme.typography.bodySmall,
            color = colors.inkMuted,
            modifier = Modifier.weight(1f),
        )
        FloconButton(onClick = onTurnOn) {
            Text(
                text = stringResource(Res.string.timeline_screenshots_turn_on),
                style = FloconTheme.typography.labelLarge,
                modifier = Modifier.padding(horizontal = FloconTheme.spacing.small),
            )
        }
    }
}

/** A screenshot of the strip, larger, shown over the graph while its thumbnail is hovered. */
@Composable
internal fun ScreenshotPopover(
    screenshot: GraphScreenshotUiModel,
    modifier: Modifier = Modifier,
) {
    ScreenshotPopover(
        filePath = screenshot.filePath,
        label = stringResource(Res.string.timeline_screenshot_at, formatScreenshotTime(screenshot.timeMs)),
        modifier = modifier,
    )
}

/** The larger screenshot with its [label], shared by the graph and the list. */
@Composable
internal fun ScreenshotPopover(
    filePath: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    Column(
        modifier = modifier
            .clip(FloconTheme.shapes.medium)
            .background(colors.bg100)
            .border(1.dp, colors.line, FloconTheme.shapes.medium)
            .padding(FloconTheme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
    ) {
        AsyncImage(
            model = File(filePath),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(width = PopoverImageWidth, height = PopoverImageHeight)
                .clip(FloconTheme.shapes.small)
                .background(colors.bg200),
        )
        Text(
            text = label,
            style = FloconTheme.typography.labelLarge,
            color = colors.ink,
        )
    }
}

// the files do not exist: the empty boxes show where the thumbnails are
@Composable
private fun ScreenshotStripPreviewContent(enabled: Boolean, withScreenshots: Boolean) {
    FloconTheme {
        ScreenshotStrip(
            screenshots = if (withScreenshots) List(6) { GraphScreenshotUiModel(timeMs = it * 10_000L, filePath = "/missing/$it.jpg") } else emptyList(),
            viewport = GraphViewport(startMs = 0.0, pxPerMs = 0.02),
            trackWidthPx = 900f,
            enabled = enabled,
            hoveredTimeMs = if (withScreenshots) 20_000L else null,
            onHover = {},
            onTurnOn = {},
        )
    }
}

@Preview
@Composable
private fun ScreenshotStripPreview() = ScreenshotStripPreviewContent(enabled = true, withScreenshots = true)

@Preview
@Composable
private fun ScreenshotStripOffPreview() = ScreenshotStripPreviewContent(enabled = false, withScreenshots = false)
