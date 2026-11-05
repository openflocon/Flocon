package io.github.openflocon.flocondesktop.features.timeline.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.timeline_screenshot_marker
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineItemUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.resources.stringResource
import java.io.File

// the line of the marker leaves from the center of the image, or from its top
private const val LineFromCenter = true

private val ThumbWidth = 44.dp
private val ThumbHeight = 92.dp
private val LineThickness = 2.dp

/**
 * A screenshot in the list, at the time it was taken: the image, and a line from it to the events with the time.
 */
@Composable
internal fun ScreenshotMarkerRow(
    item: TimelineItemUiModel.Screenshot,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = FloconTheme.spacing.medium, vertical = FloconTheme.spacing.small),
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
    ) {
        // a file that is gone (cap, temporary folder cleaned) loads nothing: the empty box stays
        AsyncImage(
            model = File(item.filePath),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(width = ThumbWidth, height = ThumbHeight)
                .clip(FloconTheme.shapes.small)
                .background(colors.bg200)
                .border(1.dp, colors.accent, FloconTheme.shapes.small),
        )
        Box(modifier = Modifier.weight(1f).height(ThumbHeight)) {
            val lineAlignment = if (LineFromCenter) Alignment.CenterStart else Alignment.TopStart
            Box(
                modifier = Modifier
                    .align(lineAlignment)
                    .fillMaxWidth()
                    .height(LineThickness)
                    .background(colors.accent),
            )
            Text(
                text = stringResource(Res.string.timeline_screenshot_marker, item.time),
                style = FloconTheme.typography.labelMedium,
                color = colors.accent,
                maxLines = 1,
                modifier = Modifier
                    .align(lineAlignment)
                    .padding(top = LineThickness + FloconTheme.spacing.tiny),
            )
        }
    }
}

@Preview
@Composable
private fun ScreenshotMarkerRowPreview() {
    FloconTheme {
        ScreenshotMarkerRow(
            item = TimelineItemUiModel.Screenshot(timestamp = 0L, day = "20/10/2026", filePath = "/missing.jpg", key = "screenshot:0", time = "12:00:30"),
        )
    }
}
