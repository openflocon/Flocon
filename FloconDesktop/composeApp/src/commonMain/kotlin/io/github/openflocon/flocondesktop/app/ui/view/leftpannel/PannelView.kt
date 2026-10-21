@file:OptIn(ExperimentalSharedTransitionApi::class)

package io.github.openflocon.flocondesktop.app.ui.view.leftpannel

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import io.github.openflocon.library.designsystem.FloconTheme
import androidx.compose.desktop.ui.tooling.preview.Preview
import org.jetbrains.compose.resources.stringResource
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.app_logo_content_description

private val NavItemHorizontalPadding = 10.dp
private val NavItemGap = 10.dp
private val NavIconSize = 16.dp

@Composable
fun PanelView(
    icon: ImageVector,
    text: String,
    isSelected: Boolean,
    expanded: Boolean,
    isEnabled: Boolean, // just make it gray but still selectable
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPinnable: Boolean = false,
    isPinned: Boolean = false,
    onTogglePin: () -> Unit = {},
) {
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    val shape = FloconTheme.shapes.medium
    val color by animateColorAsState(
        targetValue = when {
            isSelected -> FloconTheme.colors.accentSoft
            hovered -> FloconTheme.colors.bg300
            else -> FloconTheme.colors.bg300.copy(alpha = 0f)
        },
        label = "color",
    )
    val iconColor by animateColorAsState(
        targetValue = when {
            isSelected -> FloconTheme.colors.accent
            hovered -> FloconTheme.colors.ink
            else -> FloconTheme.colors.inkMuted
        }
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected || hovered) FloconTheme.colors.ink else FloconTheme.colors.inkMuted,
    )
    val lineAlpha by animateFloatAsState(
        if (isEnabled.not() && isSelected.not()) {
            0.3f
        } else 1f
    )
    Row(
        modifier = modifier
            .clip(shape)
            .background(color)
            .graphicsLayer {
                alpha = lineAlpha
            }
            .clickable(onClick = onClick, interactionSource = interactionSource, indication = null)
            // collapsed: the icon is centered in the 64dp rail
            .padding(horizontal = if (expanded) NavItemHorizontalPadding else 0.dp),
        horizontalArrangement = if (expanded) Arrangement.Start else Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.size(NavIconSize),
            imageVector = icon,
            contentDescription = stringResource(Res.string.app_logo_content_description),
            tint = iconColor,
        )
        AnimatedVisibility(
            expanded,
            enter = fadeIn(),
            exit = fadeOut(tween(100)),
        ) {
            Text(
                text = text,
                color = textColor,
                style = FloconTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                maxLines = 1,
                modifier = Modifier.padding(start = NavItemGap),
            )
        }
        // the pin is displayed on hover, and always for a pinned item
        if (expanded && isPinnable && (hovered || isPinned)) {
            Spacer(Modifier.weight(1f))
            Icon(
                modifier = Modifier
                    .size(20.dp)
                    .clip(shape)
                    .clickable(onClick = onTogglePin)
                    .padding(FloconTheme.spacing.tiny),
                imageVector = if (isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                contentDescription = if (isPinned) "Unpin" else "Pin",
                tint = FloconTheme.colors.ink,
            )
        }
    }
}

@Composable
@Preview
private fun PannelViewPreview() {
    FloconTheme {
        PanelView(
            icon = Icons.Outlined.Settings,
            text = "text",
            isSelected = false,
            onClick = {},
            expanded = false,
            isEnabled = true,
        )
    }
}

@Composable
@Preview
private fun PannelViewPreview_Selected() {
    FloconTheme {
        PanelView(
            icon = Icons.Outlined.Settings,
            text = "text",
            isSelected = true,
            onClick = {},
            expanded = false,
            isEnabled = true,
        )
    }
}

@Composable
@Preview
private fun PannelViewPreview_Disabled() {
    FloconTheme {
        PanelView(
            icon = Icons.Outlined.Settings,
            text = "text",
            isSelected = false,
            onClick = {},
            expanded = false,
            isEnabled = false,
        )
    }
}
