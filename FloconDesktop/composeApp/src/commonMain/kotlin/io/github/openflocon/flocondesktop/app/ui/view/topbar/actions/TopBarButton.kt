package io.github.openflocon.flocondesktop.app.ui.view.topbar.actions

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.WithTooltip

/**
 * [active]: the action is running (ex: recording), it takes the color of a crash to be noticed.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun TopBarButton(
    active: Boolean,
    imageVector: ImageVector,
    contentDescription: String,
    isEnabled: Boolean,
    onClicked: () -> Unit,
) {
    val colors = FloconTheme.colors
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()

    WithTooltip(tooltip = contentDescription) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(36.dp * FloconTheme.scale)
                .alpha(if (isEnabled) 1f else 0.4f)
                .clip(FloconTheme.shapes.medium)
                .background(if (hovered && isEnabled) colors.bg300 else Color.Transparent)
                .hoverable(interactionSource)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = isEnabled,
                    onClick = onClicked,
                )
                .pointerHoverIcon(PointerIcon.Hand),
        ) {
            FloconIcon(
                imageVector = imageVector,
                tint = when {
                    active -> colors.catCrash
                    hovered && isEnabled -> colors.ink
                    else -> colors.inkMuted
                },
                modifier = Modifier.size(18.dp),
            )
        }
    }
}
