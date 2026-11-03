package io.github.openflocon.flocondesktop.features.timeline.view

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Autorenew
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Sell
import androidx.compose.material.icons.outlined.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.timeline_log_level_debug
import flocondesktop.composeapp.generated.resources.timeline_log_level_error
import flocondesktop.composeapp.generated.resources.timeline_log_level_info
import flocondesktop.composeapp.generated.resources.timeline_log_level_warning
import io.github.openflocon.domain.timelinelog.models.TimelineLogLevel
import io.github.openflocon.flocondesktop.features.timeline.mapper.TimelineLogIcon
import io.github.openflocon.flocondesktop.features.timeline.mapper.color
import io.github.openflocon.flocondesktop.features.timeline.mapper.logIconOf
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

internal fun TimelineLogLevel.label(): StringResource = when (this) {
    TimelineLogLevel.Debug -> Res.string.timeline_log_level_debug
    TimelineLogLevel.Info -> Res.string.timeline_log_level_info
    TimelineLogLevel.Warning -> Res.string.timeline_log_level_warning
    TimelineLogLevel.Error -> Res.string.timeline_log_level_error
}

private fun TimelineLogIcon.imageVector(): ImageVector = when (this) {
    TimelineLogIcon.Navigation -> Icons.Outlined.Explore
    TimelineLogIcon.UserAction -> Icons.Outlined.TouchApp
    TimelineLogIcon.Lifecycle -> Icons.Outlined.Autorenew
    TimelineLogIcon.Custom -> Icons.Outlined.Sell
}

// the icon tells the type of the log, its color the level (the level is also its description)
@Composable
internal fun LogTypeIcon(
    type: String,
    level: TimelineLogLevel,
    modifier: Modifier = Modifier,
) {
    Icon(
        imageVector = logIconOf(type).imageVector(),
        contentDescription = stringResource(level.label()),
        tint = level.color(FloconTheme.colors),
        modifier = modifier.size(LogIconSize),
    )
}

private val LogIconSize = 14.dp
