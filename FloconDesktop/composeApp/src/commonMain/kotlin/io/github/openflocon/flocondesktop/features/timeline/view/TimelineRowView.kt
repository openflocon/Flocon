package io.github.openflocon.flocondesktop.features.timeline.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.timeline_source_adb
import flocondesktop.composeapp.generated.resources.timeline_source_analytics
import flocondesktop.composeapp.generated.resources.timeline_source_crash
import flocondesktop.composeapp.generated.resources.timeline_source_database
import flocondesktop.composeapp.generated.resources.timeline_source_deeplink
import flocondesktop.composeapp.generated.resources.timeline_source_network
import flocondesktop.composeapp.generated.resources.timeline_source_table
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineRowUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

internal fun TimelineSource.label(): StringResource = when (this) {
    TimelineSource.Network -> Res.string.timeline_source_network
    TimelineSource.Database -> Res.string.timeline_source_database
    TimelineSource.Analytics -> Res.string.timeline_source_analytics
    TimelineSource.Crash -> Res.string.timeline_source_crash
    TimelineSource.Table -> Res.string.timeline_source_table
    TimelineSource.Deeplink -> Res.string.timeline_source_deeplink
    TimelineSource.Adb -> Res.string.timeline_source_adb
}

@Composable
internal fun TimelineRowView(
    row: TimelineRowUiModel,
    onClick: (TimelineRowUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(FloconTheme.shapes.medium)
            .background(FloconTheme.colorPalette.primary)
            .clickable { onClick(row) }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = row.time,
            style = FloconTheme.typography.bodySmall,
            color = FloconTheme.colorPalette.onPrimary,
            maxLines = 1,
            modifier = Modifier.width(96.dp),
        )
        SourceBadge(
            source = row.source,
            modifier = Modifier.width(80.dp),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = row.title,
                style = FloconTheme.typography.bodyMedium,
                color = FloconTheme.colorPalette.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(),
            )
            row.subtitle?.let {
                Text(
                    text = it,
                    style = FloconTheme.typography.bodySmall,
                    color = FloconTheme.colorPalette.onPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
internal fun SourceBadge(
    source: TimelineSource,
    modifier: Modifier = Modifier,
) {
    Text(
        text = stringResource(source.label()),
        style = FloconTheme.typography.labelSmall,
        color = FloconTheme.colorPalette.onSecondary,
        maxLines = 1,
        modifier = modifier
            .clip(FloconTheme.shapes.small)
            .background(FloconTheme.colorPalette.secondary)
            .padding(horizontal = 8.dp, vertical = 2.dp),
    )
}
