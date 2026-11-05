package io.github.openflocon.flocondesktop.features.timeline.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.auto_scroll
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineAction
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconToggleSwitch
import org.jetbrains.compose.resources.stringResource

/**
 * The auto scroll, next to the screenshots: the list and the graph follow the newest event, the graph follows the present
 * moment. The graph turns it off when the user moves back in time or selects an event, and back on when the user reaches
 * the present moment again. Turning it on jumps to now.
 */
@Composable
internal fun AutoScrollControl(
    enabled: Boolean,
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
            imageVector = Icons.Outlined.PlayCircle,
            tint = if (enabled) colors.ink else colors.inkMuted,
        )
        Text(
            text = stringResource(Res.string.auto_scroll),
            style = FloconTheme.typography.labelMedium,
            color = if (enabled) colors.ink else colors.inkMuted,
            maxLines = 1,
        )
        FloconToggleSwitch(
            checked = enabled,
            onCheckedChange = { onAction(TimelineAction.ToggleAutoScroll(it)) },
        )
    }
}
