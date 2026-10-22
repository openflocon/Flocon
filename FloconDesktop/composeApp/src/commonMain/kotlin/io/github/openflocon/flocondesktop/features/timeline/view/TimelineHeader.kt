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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.menu_item_timeline
import flocondesktop.composeapp.generated.resources.timeline_counter_crashes
import flocondesktop.composeapp.generated.resources.timeline_counter_events
import flocondesktop.composeapp.generated.resources.timeline_counter_http_errors
import flocondesktop.composeapp.generated.resources.timeline_counter_sessions
import flocondesktop.composeapp.generated.resources.timeline_live
import io.github.openflocon.flocondesktop.common.ui.featuredoc.FeatureDoc
import io.github.openflocon.flocondesktop.common.ui.featuredoc.FeatureDocButton
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineSummaryUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

/**
 * The title, the time range covered and the counters of what the filters display.
 * [isLive]: the device is connected.
 */
@Composable
internal fun TimelineHeader(
    summary: TimelineSummaryUiModel,
    isLive: Boolean,
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
        Counter(
            count = summary.eventCount,
            label = pluralStringResource(Res.plurals.timeline_counter_events, summary.eventCount),
        )
        Counter(
            count = summary.sessionCount,
            label = pluralStringResource(Res.plurals.timeline_counter_sessions, summary.sessionCount),
        )
        Counter(
            count = summary.httpErrorCount,
            label = pluralStringResource(Res.plurals.timeline_counter_http_errors, summary.httpErrorCount),
            highlight = colors.statusWarning,
        )
        Counter(
            count = summary.crashCount,
            label = pluralStringResource(Res.plurals.timeline_counter_crashes, summary.crashCount),
            highlight = colors.catCrash,
        )
        FeatureDocButton(doc = FeatureDoc.Timeline)
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

// [highlight] colors the number when there is something to look at, an empty counter stays neutral
@Composable
private fun Counter(
    count: Int,
    label: String,
    modifier: Modifier = Modifier,
    highlight: Color? = null,
) {
    val colors = FloconTheme.colors
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End,
    ) {
        Text(
            text = count.toString(),
            style = monoStyle().copy(fontSize = FloconTheme.typography.titleMedium.fontSize),
            fontWeight = FontWeight.SemiBold,
            color = if (highlight != null && count > 0) highlight else colors.ink,
            maxLines = 1,
        )
        Text(
            text = label,
            style = FloconTheme.typography.labelSmall,
            color = colors.inkFaint,
            maxLines = 1,
        )
    }
}
