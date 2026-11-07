package io.github.openflocon.flocondesktop.features.timeline.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
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
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.resources.stringResource
import java.io.File

private val ThumbWidth = 40.dp
private val ThumbHeight = 56.dp

/**
 * A screenshot in the list, at the time it was taken. It has the layout of an event: the time, then the thumbnail where
 * the category goes. Hovering it shows it larger, and [onHoverChange] lets the list draw the bar of the events it covers.
 */
@Composable
internal fun ScreenshotMarkerRow(
    item: TimelineItemUiModel.Screenshot,
    onHoverChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    val spacing = FloconTheme.spacing
    val shape = FloconTheme.shapes.medium
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    LaunchedEffect(hovered) { onHoverChange(hovered) }
    DisposableEffect(Unit) { onDispose { onHoverChange(false) } }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .hoverable(interactionSource)
            .padding(horizontal = spacing.compact, vertical = spacing.tiny),
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
        // the free space where the bar of the covered events goes, like in the other rows
        Spacer(Modifier.width(railGutter()))
        Row(
            modifier = Modifier
                .weight(1f)
                .heightIn(min = ThumbHeight)
                .background(if (hovered) colors.accentSoft else colors.bg100, shape)
                .padding(horizontal = spacing.compact, vertical = spacing.tiny),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.small),
        ) {
            Box(modifier = Modifier.width(FloconTheme.sizes.categoryColumn)) {
                // a file that is gone (cap, temporary folder cleaned) loads nothing: the empty box stays
                AsyncImage(
                    model = File(item.filePath),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(width = ThumbWidth, height = ThumbHeight)
                        .clip(FloconTheme.shapes.small)
                        .background(colors.bg200),
                )
            }
            Text(
                text = stringResource(Res.string.timeline_screenshot_label),
                style = FloconTheme.typography.bodyMedium,
                color = colors.ink,
                maxLines = 1,
            )
        }
    }

    if (hovered) {
        // the screenshot larger, like the thumbnails of the graph, on the right of the thumbnail
        val density = LocalDensity.current
        val thumbnailEnd = with(density) {
            (FloconTheme.spacing.compact * 2 + FloconTheme.sizes.timeColumn + FloconTheme.sizes.categoryColumn + railGutter() + FloconTheme.spacing.small * 3).roundToPx()
        }
        Popup(popupPositionProvider = remember(thumbnailEnd) { SideOfRowPositionProvider(thumbnailEnd) }) {
            ScreenshotPopover(
                filePath = item.filePath,
                label = stringResource(Res.string.timeline_screenshot_at, item.time),
            )
        }
    }
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
            onHoverChange = {},
        )
    }
}
