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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
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
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun PanelView(
    icon: ImageVector,
    text: String,
    isSelected: Boolean,
    expanded: Boolean,
    isEnabled: Boolean, // just make it gray but still selectable
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    val shape = FloconTheme.shapes.medium
    val color by animateColorAsState(
        targetValue = when {
            isSelected -> FloconTheme.colorPalette.accent
            hovered -> FloconTheme.colorPalette.secondary
            else -> FloconTheme.colorPalette.surface.copy(alpha = 0f)
        },
        label = "color",
    )
    val iconColor by animateColorAsState(
        targetValue = when {
            isSelected -> FloconTheme.colorPalette.onAccent
            hovered -> FloconTheme.colorPalette.onSecondary
            else -> FloconTheme.colorPalette.onPrimary
        }
    )

    val lineAlpha by animateFloatAsState(
        if (isEnabled.not() && isSelected.not()) {
            0.3f
        } else 1f
    )
    val horizontalPadding = 12.dp

    Row(
        modifier = modifier
            .clip(shape)
            .background(color)
            .graphicsLayer {
                alpha = lineAlpha
            }
            .clickable(onClick = onClick, interactionSource = interactionSource, indication = null)
            .padding(horizontal = horizontalPadding, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier
                .size(PanelContentMinSize - horizontalPadding.times(2))
                .padding(4.dp),
            imageVector = icon,
            contentDescription = "Description de mon image",
            tint = iconColor,
        )
        AnimatedVisibility(
            expanded,
            enter = fadeIn(),
            exit = fadeOut(tween(100)),
        ) {
            Text(
                text = text,
                color = FloconTheme.colorPalette.onSurface,
                style = FloconTheme.typography.bodyMedium,
                maxLines = 1,
                modifier = Modifier.padding(start = 12.dp),
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
