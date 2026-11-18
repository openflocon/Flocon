package io.github.openflocon.flocondesktop.features.timeline.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.auto_scroll
import flocondesktop.composeapp.generated.resources.timeline_auto_scroll_follow
import flocondesktop.composeapp.generated.resources.timeline_auto_scroll_following
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineAction
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIcon
import org.jetbrains.compose.resources.stringResource

private val BorderWidth = 1.dp
private val IconSize = 16.dp
private const val OutlineAlpha = 0.55f
private const val BadgeAlpha = 0.14f

/**
 * The auto scroll, next to the screenshots and drawn like them, in the same colors: one pill with two looks.
 * - off: outline and a "Follow" badge, a click turns it on and the graph jumps to now
 * - on: fill, and a "Following" badge, a click turns it off
 * The graph turns it off when the user moves back in time or selects an event, and back on when the user reaches the
 * present moment again.
 */
@Composable
internal fun AutoScrollControl(
    enabled: Boolean,
    onAction: (TimelineAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    val shape = FloconTheme.shapes.medium
    Row(
        modifier = modifier
            .height(FloconTheme.sizes.controlHeight)
            .clip(shape)
            .border(BorderWidth, colors.accent.copy(alpha = OutlineAlpha), shape),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AutoScrollHead(
            on = enabled,
            onClick = { onAction(TimelineAction.ToggleAutoScroll(!enabled)) },
        )
        if (enabled) {
            AutoScrollFollowingBadge(onClick = { onAction(TimelineAction.ToggleAutoScroll(false)) })
        } else {
            AutoScrollFollowBadge(onClick = { onAction(TimelineAction.ToggleAutoScroll(true)) })
        }
    }
}

/** The play icon and the label: it turns the auto scroll on and off. */
@Composable
private fun AutoScrollHead(
    on: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val background = when {
        on -> if (hovered) colors.accent.copy(alpha = 0.88f) else colors.accent
        else -> if (hovered) colors.accent.copy(alpha = BadgeAlpha) else Color.Transparent
    }
    val contentColor = if (on) colors.accentInk else colors.ink
    Row(
        modifier = modifier
            .fillMaxHeight()
            .background(background)
            .hoverable(interaction)
            .pointerHoverIcon(PointerIcon.Hand)
            .clickable(
                role = Role.Switch,
                interactionSource = interaction,
                // the hover is the feedback
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = FloconTheme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
    ) {
        FloconIcon(
            imageVector = Icons.Outlined.PlayCircle,
            tint = if (on) contentColor else colors.accent,
            modifier = Modifier.size(IconSize),
        )
        Text(
            text = stringResource(Res.string.auto_scroll),
            style = FloconTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            color = contentColor,
            maxLines = 1,
        )
    }
}

/** "Follow": the auto scroll is off, a click turns it on. */
@Composable
private fun AutoScrollFollowBadge(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    Row(
        modifier = modifier
            .fillMaxHeight()
            .background(if (hovered) colors.accent.copy(alpha = 0.88f) else colors.accent)
            .hoverable(interaction)
            .pointerHoverIcon(PointerIcon.Hand)
            .clickable(
                role = Role.Button,
                interactionSource = interaction,
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = FloconTheme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(Res.string.timeline_auto_scroll_follow),
            style = FloconTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = colors.accentInk,
            maxLines = 1,
        )
    }
}

/** "Following": the graph follows the present moment, a click turns it off. */
@Composable
private fun AutoScrollFollowingBadge(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    Row(
        modifier = modifier
            .fillMaxHeight()
            .background(colors.accent.copy(alpha = if (hovered) BadgeAlpha * 2 else BadgeAlpha))
            .hoverable(interaction)
            .pointerHoverIcon(PointerIcon.Hand)
            .clickable(
                role = Role.Button,
                interactionSource = interaction,
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = FloconTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.compact),
    ) {
        CategoryDot(color = colors.accent)
        Text(
            text = stringResource(Res.string.timeline_auto_scroll_following),
            style = FloconTheme.typography.labelMedium,
            color = colors.accent,
            maxLines = 1,
        )
    }
}

@Preview
@Composable
private fun AutoScrollControlOnPreview() {
    FloconTheme {
        AutoScrollControl(enabled = true, onAction = {})
    }
}

@Preview
@Composable
private fun AutoScrollControlOffPreview() {
    FloconTheme {
        AutoScrollControl(enabled = false, onAction = {})
    }
}
