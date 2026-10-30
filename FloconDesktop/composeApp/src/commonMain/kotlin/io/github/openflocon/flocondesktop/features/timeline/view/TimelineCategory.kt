package io.github.openflocon.flocondesktop.features.timeline.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.theme.EventCategory
import org.jetbrains.compose.resources.stringResource

internal fun TimelineSource.category(): EventCategory = when (this) {
    TimelineSource.Network -> EventCategory.Network
    TimelineSource.Database -> EventCategory.Database
    TimelineSource.Analytics -> EventCategory.Analytics
    TimelineSource.Accessibility -> EventCategory.Accessibility
    TimelineSource.WebView -> EventCategory.WebView
    TimelineSource.Crash -> EventCategory.Crash
    TimelineSource.Table -> EventCategory.Table
    TimelineSource.Deeplink -> EventCategory.Deeplink
    TimelineSource.Adb -> EventCategory.Adb
    TimelineSource.AppStart -> EventCategory.AppStart
}

// the color of the category and its soft background
@Composable
internal fun TimelineSource.categoryColors(): Pair<Color, Color> = FloconTheme.colors.category(category())

// the dot of a category: plain when the category is displayed, a ring otherwise
@Composable
internal fun CategoryDot(
    color: Color,
    modifier: Modifier = Modifier,
    filled: Boolean = true,
) {
    Box(
        modifier = modifier
            .size(6.dp)
            .clip(CircleShape)
            .then(if (filled) Modifier.background(color) else Modifier.border(1.5.dp, color, CircleShape))
    )
}

// names the category of an event: the color is never alone
@Composable
internal fun EventBadge(
    source: TimelineSource,
    modifier: Modifier = Modifier,
) {
    val (color, soft) = source.categoryColors()
    Row(
        modifier = modifier
            .clip(FloconTheme.shapes.small)
            .background(soft)
            .padding(start = FloconTheme.spacing.compact, end = FloconTheme.spacing.small, top = FloconTheme.spacing.tiny, bottom = FloconTheme.spacing.tiny),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.extraSmall),
    ) {
        CategoryDot(color = color)
        Text(
            text = stringResource(source.label()),
            style = FloconTheme.typography.labelMedium,
            color = color,
            maxLines = 1,
        )
    }
}

// shows or hides a category of events, with the number of events it holds
@Composable
internal fun SourceChip(
    source: TimelineSource,
    count: Int,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    val (color, soft) = source.categoryColors()
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(if (selected) soft else colors.bg200)
            .border(1.dp, if (selected) color else colors.lineStrong, CircleShape)
            .clickable(onClick = onClick)
            .padding(horizontal = FloconTheme.spacing.medium, vertical = FloconTheme.spacing.compact),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.compact),
    ) {
        CategoryDot(color = color, filled = selected)
        Text(
            text = stringResource(source.label()),
            style = FloconTheme.typography.labelMedium,
            color = if (selected) colors.ink else colors.inkMuted,
            maxLines = 1,
        )
        Text(
            text = count.toString(),
            style = FloconTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
            color = if (selected) color else colors.inkFaint,
            maxLines = 1,
        )
    }
}
