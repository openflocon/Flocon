package io.github.openflocon.flocondesktop.features.timeline.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Smartphone
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import coil3.compose.AsyncImage
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.timeline_screenshot_at
import flocondesktop.composeapp.generated.resources.timeline_screenshot_label
import io.github.openflocon.flocondesktop.features.timeline.graph.ScreenshotPopover
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineItemUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineScreenshotContextUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIcon
import org.jetbrains.compose.resources.stringResource
import java.io.File
import kotlin.io.encoding.Base64
import org.jetbrains.skia.Image as SkiaImage

private val PhoneWidth = 62.dp
private val PhoneHeight = 112.dp
private val PhoneBezel = 3.dp
private val DashLength = 6.dp
private val TailWidth = 40.dp
private val AppIconSize = 20.dp

/**
 * A screenshot in the list, at the time it was taken: a dashed separator, and the screenshot in a phone frame under it with
 * the name of the screen ([screenName], the last navigation before it), the app and the device ([context]).
 * Hovering it shows it larger, and [onHoverChange] lets the list draw the bar of the events it covers.
 */
@Composable
internal fun ScreenshotMarkerRow(
    item: TimelineItemUiModel.Screenshot,
    screenName: String?,
    context: TimelineScreenshotContextUiModel?,
    onHoverChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    val spacing = FloconTheme.spacing
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    LaunchedEffect(hovered) { onHoverChange(hovered) }
    DisposableEffect(Unit) { onDispose { onHoverChange(false) } }

    val label = stringResource(Res.string.timeline_screenshot_label)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .hoverable(interactionSource)
            .padding(horizontal = spacing.compact, vertical = spacing.small),
        verticalArrangement = Arrangement.spacedBy(spacing.small),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.small),
        ) {
            Text(
                text = item.time,
                style = monoStyle(),
                color = colors.accent,
                maxLines = 1,
                modifier = Modifier.width(FloconTheme.sizes.timeColumn),
            )
            DashedLine(modifier = Modifier.weight(1f))
            Text(
                text = label.uppercase(),
                style = FloconTheme.typography.labelSmall,
                color = colors.inkMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            DashedLine(modifier = Modifier.width(TailWidth))
        }
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(spacing.small),
        ) {
            // the time column and the free space where the bar of the covered events goes, like in the other rows
            Spacer(Modifier.width(FloconTheme.sizes.timeColumn))
            Spacer(Modifier.width(railGutter()))
            PhoneFrame(item = item, hovered = hovered, modifier = Modifier.padding(start = spacing.medium))
            if (screenName != null || context != null) {
                ScreenshotContext(
                    screenName = screenName,
                    context = context,
                    modifier = Modifier.padding(start = spacing.small, bottom = spacing.tiny),
                )
            }
        }
    }

    if (hovered) {
        // the screenshot larger, like the thumbnails of the graph, on the right of the thumbnail
        val density = LocalDensity.current
        val thumbnailEnd = with(density) {
            (
                FloconTheme.spacing.compact + FloconTheme.sizes.timeColumn + railGutter() + FloconTheme.spacing.small * 2 +
                    FloconTheme.spacing.medium * 2 + PhoneWidth
                ).roundToPx()
        }
        Popup(popupPositionProvider = remember(thumbnailEnd) { SideOfRowPositionProvider(thumbnailEnd) }) {
            ScreenshotPopover(
                filePath = item.filePath,
                label = stringResource(Res.string.timeline_screenshot_at, item.time),
            )
        }
    }
}

// what the screenshot shows: the screen, the app with its icon, and the device
@Composable
private fun ScreenshotContext(
    screenName: String?,
    context: TimelineScreenshotContextUiModel?,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
    ) {
        if (screenName != null) {
            Text(
                text = screenName,
                style = FloconTheme.typography.titleSmall,
                color = colors.ink,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (context != null) {
            if (context.appName != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
                ) {
                    AppIcon(name = context.appName, iconEncoded = context.appIconEncoded)
                    Text(
                        text = context.appName,
                        style = FloconTheme.typography.bodySmall,
                        color = colors.ink,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
            ) {
                FloconIcon(
                    imageVector = Icons.Outlined.Smartphone,
                    tint = colors.inkMuted,
                    modifier = Modifier.size(AppIconSize),
                )
                Text(
                    text = "${context.deviceName} · ${context.platform}",
                    style = FloconTheme.typography.bodySmall,
                    color = colors.inkMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

// the icon the app sent, or a soft square with its first letter
@Composable
private fun AppIcon(
    name: String,
    iconEncoded: String?,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    val bitmap = remember(iconEncoded) {
        iconEncoded?.let { encoded ->
            runCatching { SkiaImage.makeFromEncoded(Base64.decode(encoded)).toComposeImageBitmap() }.getOrNull()
        }
    }
    val shape = FloconTheme.shapes.small
    if (bitmap != null) {
        Image(bitmap = bitmap, contentDescription = null, modifier = modifier.size(AppIconSize).clip(shape))
    } else {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier.size(AppIconSize).clip(shape).background(colors.catTableSoft),
        ) {
            Text(
                text = name.take(1).uppercase(),
                style = FloconTheme.typography.labelSmall,
                color = colors.catTable,
            )
        }
    }
}

// the screenshot like on a phone: a dark bezel, and a shadow to lift it from the list
@Composable
private fun PhoneFrame(
    item: TimelineItemUiModel.Screenshot,
    hovered: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    val shape = FloconTheme.shapes.large
    // a file that is gone (cap, temporary folder cleaned) loads nothing: the frame stays empty
    Box(
        modifier = modifier
            .size(width = PhoneWidth, height = PhoneHeight)
            .shadow(elevation = 8.dp, shape = shape)
            .clip(shape)
            .border(PhoneBezel, colors.bg300, shape)
            .border(1.dp, if (hovered) colors.accent else colors.line, shape),
    ) {
        AsyncImage(
            model = File(item.filePath),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(width = PhoneWidth, height = PhoneHeight).padding(PhoneBezel).clip(shape),
        )
    }
}

@Composable
private fun DashedLine(modifier: Modifier = Modifier) {
    val color = FloconTheme.colors.line
    val dash = DashLength.value
    Box(
        modifier = modifier
            .height(1.dp)
            .drawBehind {
                drawLine(
                    color = color,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(dash.dp.toPx(), dash.dp.toPx())),
                )
            },
    )
}

/** Next to the row, [startOffset] pixels after its start, centered on it and kept inside the window. */
private class SideOfRowPositionProvider(private val startOffset: Int) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset {
        val x = (anchorBounds.left + startOffset).coerceIn(0, (windowSize.width - popupContentSize.width).coerceAtLeast(0))
        val y = (anchorBounds.center.y - popupContentSize.height / 2).coerceIn(0, (windowSize.height - popupContentSize.height).coerceAtLeast(0))
        return IntOffset(x, y)
    }
}

@Preview
@Composable
private fun ScreenshotMarkerRowPreview() {
    FloconTheme {
        ScreenshotMarkerRow(
            item = TimelineItemUiModel.Screenshot(timestamp = 0L, day = "20/10/2026", filePath = "/missing.jpg", key = "screenshot:0", time = "12:00:30"),
            screenName = "FeedScreen",
            context = TimelineScreenshotContextUiModel(appName = "Flocon Sample", appIconEncoded = null, deviceName = "Pixel 8", platform = "Android"),
            onHoverChange = {},
        )
    }
}
