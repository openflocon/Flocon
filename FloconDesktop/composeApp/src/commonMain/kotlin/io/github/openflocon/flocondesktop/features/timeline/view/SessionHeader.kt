package io.github.openflocon.flocondesktop.features.timeline.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.timeline_app_started
import flocondesktop.composeapp.generated.resources.timeline_session_events
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineRowUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

/**
 * An app start: a diamond on the rail and a banner, at the time the app started.
 * [eventCount] is the number of events of the session displayed, null while unknown.
 */
@Composable
internal fun SessionHeader(
    row: TimelineRowUiModel,
    eventCount: Int?,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    val spacing = FloconTheme.spacing

    // same paddings and gaps as a row, so the rail goes through the banner like through the rows
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(horizontal = spacing.compact, vertical = spacing.tiny),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.small),
    ) {
        Text(
            text = row.time,
            style = monoStyle(),
            fontWeight = FontWeight.Medium,
            color = colors.ink,
            maxLines = 1,
            modifier = Modifier.width(FloconTheme.sizes.timeColumn),
        )
        val line = colors.line
        val overflow = spacing.tiny + spacing.tiny
        Box(
            modifier = Modifier
                .width(FloconTheme.sizes.markerColumn)
                .fillMaxHeight()
                .drawBehind {
                    val x = size.width / 2
                    drawLine(
                        color = line,
                        start = Offset(x, -overflow.toPx()),
                        end = Offset(x, size.height + overflow.toPx()),
                        strokeWidth = 1.dp.toPx(),
                    )
                },
            contentAlignment = Alignment.Center,
        ) {
            Spacer(
                Modifier
                    .size(14.dp)
                    .rotate(45f)
                    .background(colors.accent, FloconTheme.shapes.small)
            )
        }
        Row(
            modifier = Modifier
                .weight(1f)
                .background(colors.accentSoft, FloconTheme.shapes.medium)
                .padding(horizontal = spacing.medium, vertical = spacing.small),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.medium),
        ) {
            Text(
                text = stringResource(Res.string.timeline_app_started),
                style = FloconTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = colors.ink,
                maxLines = 1,
            )
            Spacer(Modifier.weight(1f))
            if (eventCount != null) {
                Text(
                    text = pluralStringResource(Res.plurals.timeline_session_events, eventCount, eventCount),
                    style = FloconTheme.typography.labelMedium,
                    color = colors.inkMuted,
                    maxLines = 1,
                )
            }
        }
    }
}
