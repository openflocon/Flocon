package io.github.openflocon.flocondesktop.features.timeline.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.timeline_screenshots
import flocondesktop.composeapp.generated.resources.timeline_screenshots_failing
import flocondesktop.composeapp.generated.resources.timeline_screenshots_interval
import io.github.openflocon.domain.timeline.models.TimelineScreenshotIntervalsSeconds
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineAction
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconSegmentedTextToggle
import io.github.openflocon.library.designsystem.components.FloconToggleSwitch
import io.github.openflocon.library.designsystem.components.WithTooltip
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.stringResource

/**
 * The screenshots of the app taken at a regular interval, shown next to the events: a switch, and the interval once on.
 * [failing]: the last captures did not work.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun ScreenshotsControl(
    enabled: Boolean,
    intervalSeconds: Int,
    failing: Boolean,
    onAction: (TimelineAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
    ) {
        FloconIcon(
            imageVector = Icons.Outlined.PhotoCamera,
            tint = if (enabled) colors.ink else colors.inkMuted,
        )
        Text(
            text = stringResource(Res.string.timeline_screenshots),
            style = FloconTheme.typography.labelMedium,
            color = if (enabled) colors.ink else colors.inkMuted,
            maxLines = 1,
        )
        FloconToggleSwitch(
            checked = enabled,
            onCheckedChange = { onAction(TimelineAction.SetScreenshots(it)) },
        )
        if (enabled) {
            val labels = TimelineScreenshotIntervalsSeconds.map { stringResource(Res.string.timeline_screenshots_interval, it) }
            FloconSegmentedTextToggle(
                options = labels.toImmutableList(),
                selectedIndex = TimelineScreenshotIntervalsSeconds.indexOf(intervalSeconds).coerceAtLeast(0),
                onSelect = { onAction(TimelineAction.SetScreenshotInterval(TimelineScreenshotIntervalsSeconds[it])) },
            )
        }
        // the warning is about the capture: it makes no sense next to a switch that is off
        if (failing && enabled) {
            WithTooltip(tooltip = stringResource(Res.string.timeline_screenshots_failing)) {
                CategoryDot(color = colors.statusWarning)
            }
        }
    }
}

@Composable
private fun ScreenshotsControlPreviewContent(enabled: Boolean, failing: Boolean) {
    FloconTheme {
        ScreenshotsControl(
            enabled = enabled,
            intervalSeconds = 10,
            failing = failing,
            onAction = {},
        )
    }
}

@Preview
@Composable
private fun ScreenshotsControlOnPreview() = ScreenshotsControlPreviewContent(enabled = true, failing = false)

@Preview
@Composable
private fun ScreenshotsControlOffPreview() = ScreenshotsControlPreviewContent(enabled = false, failing = false)

@Preview
@Composable
private fun ScreenshotsControlFailingPreview() = ScreenshotsControlPreviewContent(enabled = true, failing = true)
