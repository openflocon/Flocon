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
import flocondesktop.composeapp.generated.resources.timeline_screenshots_unavailable
import io.github.openflocon.domain.timeline.models.TimelineScreenshotIntervalsSeconds
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineAction
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineScreenshotsStatus
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconSegmentedTextToggle
import io.github.openflocon.library.designsystem.components.FloconToggleSwitch
import io.github.openflocon.library.designsystem.components.WithTooltip
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.stringResource

/**
 * The screenshots of the app taken at a regular interval, shown next to the events: a switch, and the interval once on.
 * [status]: the last captures did not work, or the device cannot take them.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun ScreenshotsControl(
    enabled: Boolean,
    intervalSeconds: Int,
    status: TimelineScreenshotsStatus,
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
        if (enabled) {
            val tooltip = when (status) {
                TimelineScreenshotsStatus.Working -> null
                TimelineScreenshotsStatus.Failing -> stringResource(Res.string.timeline_screenshots_failing)
                TimelineScreenshotsStatus.Unavailable -> stringResource(Res.string.timeline_screenshots_unavailable)
            }
            if (tooltip != null) {
                WithTooltip(tooltip = tooltip) {
                    CategoryDot(color = colors.statusWarning)
                }
            }
        }
    }
}

@Composable
private fun ScreenshotsControlPreviewContent(enabled: Boolean, status: TimelineScreenshotsStatus) {
    FloconTheme {
        ScreenshotsControl(
            enabled = enabled,
            intervalSeconds = 10,
            status = status,
            onAction = {},
        )
    }
}

@Preview
@Composable
private fun ScreenshotsControlOnPreview() = ScreenshotsControlPreviewContent(enabled = true, status = TimelineScreenshotsStatus.Working)

@Preview
@Composable
private fun ScreenshotsControlOffPreview() = ScreenshotsControlPreviewContent(enabled = false, status = TimelineScreenshotsStatus.Working)

@Preview
@Composable
private fun ScreenshotsControlFailingPreview() = ScreenshotsControlPreviewContent(enabled = true, status = TimelineScreenshotsStatus.Failing)

@Preview
@Composable
private fun ScreenshotsControlUnavailablePreview() = ScreenshotsControlPreviewContent(enabled = true, status = TimelineScreenshotsStatus.Unavailable)
