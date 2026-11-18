package io.github.openflocon.library.designsystem.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.openflocon.library.designsystem.FloconTheme

private val ThumbInset = 2.dp

/**
 * Pill shaped on / off switch: accent track and ink thumb when [checked], neutral otherwise.
 * Without [onCheckedChange] it only displays the state, for a parent that handles the click (a whole row for example).
 */
@Composable
fun FloconToggleSwitch(
    checked: Boolean,
    modifier: Modifier = Modifier,
    onCheckedChange: ((Boolean) -> Unit)? = null,
    width: Dp = 36.dp,
    height: Dp = 20.dp,
) {
    val colors = FloconTheme.colors
    val thumbSize = height - ThumbInset * 2
    val thumbOffset by animateDpAsState(if (checked) width - thumbSize - ThumbInset else ThumbInset)
    val trackColor by animateColorAsState(if (checked) colors.accent else colors.bg300)
    val thumbColor by animateColorAsState(if (checked) colors.accentInk else colors.inkMuted)
    val shape = RoundedCornerShape(50)
    Box(
        modifier = modifier
            .size(width = width, height = height)
            .background(trackColor, shape)
            .border(1.dp, if (checked) colors.accent else colors.lineStrong, shape)
            .clip(shape)
            .then(
                if (onCheckedChange != null) {
                    Modifier
                        .toggleable(
                            value = checked,
                            role = Role.Switch,
                            // no hover / press highlight: it is tinted with the accent color
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onValueChange = onCheckedChange,
                        )
                        .pointerHoverIcon(PointerIcon.Hand)
                } else {
                    Modifier
                },
            ),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            Modifier
                .offset(x = thumbOffset)
                .size(thumbSize)
                .background(thumbColor, CircleShape),
        )
    }
}
