package io.github.openflocon.flocondesktop.features.timeline.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.menu_item_timeline
import flocondesktop.composeapp.generated.resources.timeline_live
import io.github.openflocon.flocondesktop.common.ui.featuredoc.FeatureDoc
import io.github.openflocon.flocondesktop.common.ui.featuredoc.FeatureDocButton
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineAction
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineScreenshotsStatus
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineSettingsUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineSummaryUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.resources.stringResource

/**
 * The title, the time range covered and the counters of what the filters display.
 * [isLive]: the device is connected.
 */
@Composable
internal fun TimelineHeader(
    summary: TimelineSummaryUiModel,
    isLive: Boolean,
    settings: TimelineSettingsUiModel,
    screenshotsStatus: TimelineScreenshotsStatus,
    onAction: (TimelineAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.large),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.tiny)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.medium),
            ) {
                Text(
                    text = stringResource(Res.string.menu_item_timeline),
                    style = FloconTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.ink,
                    maxLines = 1,
                )
                if (isLive) {
                    LivePill()
                }
            }
            summary.range?.let {
                Text(
                    text = it,
                    style = monoStyle(),
                    color = colors.inkFaint,
                    maxLines = 1,
                )
            }
        }
        Spacer(Modifier.weight(1f))
        ScreenshotsControl(
            enabled = settings.screenshots,
            intervalSeconds = settings.screenshotIntervalSeconds,
            status = screenshotsStatus,
            onAction = onAction,
        )
        FeatureDocButton(doc = FeatureDoc.Timeline)
        TimelineOverflowMenu(
            settings = settings,
            onAction = onAction,
        )
    }
}

@Composable
private fun LivePill(modifier: Modifier = Modifier) {
    val color = FloconTheme.colors.statusSuccess
    Row(
        modifier = modifier
            .background(color.copy(alpha = 0.14f), CircleShape)
            .padding(horizontal = FloconTheme.spacing.small, vertical = FloconTheme.spacing.tiny),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.compact),
    ) {
        CategoryDot(color = color)
        Text(
            text = stringResource(Res.string.timeline_live),
            style = FloconTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = color,
            maxLines = 1,
        )
    }
}
