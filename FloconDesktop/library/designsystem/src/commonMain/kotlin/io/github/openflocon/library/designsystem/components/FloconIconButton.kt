@file:OptIn(ExperimentalFoundationApi::class)

package io.github.openflocon.library.designsystem.components

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.TooltipPlacement
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import io.github.openflocon.library.designsystem.FloconTheme

private val Size = 32.dp
private val Padding = 8.dp

/**
 * Cannot use IconButton because of the indication
 */
@Composable
fun FloconIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .size(Size)
            .clip(FloconTheme.shapes.medium)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(all = 8.dp)
    ) {
        CompositionLocalProvider(LocalContentColor provides FloconTheme.colorPalette.onBackground) {
            content()
        }
    }
}

@Composable
fun FloconIconToggleButton(
    value: Boolean,
    onValueChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    tooltip: String? = null,
    content: @Composable () -> Unit
) {
    val shape = FloconTheme.shapes.medium
    val transition = updateTransition(value)
    val contentColor by transition.animateColor {
        if (it) {
            FloconTheme.colorPalette.onSecondary
        } else {
            FloconTheme.colorPalette.onPrimary
        }
    }
    val borderColor by transition.animateColor {
        if (it) {
            FloconTheme.colorPalette.onSecondary
        } else {
            Color.Transparent
        }
    }
    val containerColor by transition.animateColor {
        if (it) {
            FloconTheme.colorPalette.secondary
        } else {
            FloconTheme.colorPalette.primary
        }
    }

    TooltipArea(
        tooltip = {
            if (tooltip != null) {
                Text(
                    text = tooltip,
                    style = FloconTheme.typography.labelSmall,
                    modifier = Modifier
                        .clip(FloconTheme.shapes.small)
                        .background(FloconTheme.colorPalette.primary)
                        .padding(vertical = 2.dp, horizontal = 4.dp)
                )
            }
        },
        delayMillis = 100,
        tooltipPlacement = TooltipPlacement.ComponentRect(
            offset = DpOffset(x = 0.dp, y = 2.dp)
        )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .size(Size)
                .clip(shape)
                .background(color = containerColor)
                .border(
                    width = 1.dp,
                    color = borderColor,
                    shape = shape
                )
                .toggleable(
                    value = value,
                    onValueChange = onValueChange
                )
                .padding(Padding)
        ) {
            CompositionLocalProvider(LocalContentColor provides contentColor) {
                content()
            }
        }
    }
}

@Composable
fun FloconSmallIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clickable(
                enabled = enabled,
                onClick = onClick
            )
            .graphicsLayer {
                alpha = if (enabled) 1f else .5f
            },
        content = content
    )
}

@Composable
fun FloconIconButton(
    imageVector: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    FloconIconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
    ) {
        FloconIcon(
            imageVector = imageVector
        )
    }
}

@Composable
fun FloconSmallIconButton(
    imageVector: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = LocalContentColor.current,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    contentDescription: String? = null,
    enabled: Boolean = true
) {
    FloconSmallIconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.padding(contentPadding),
    ) {
        Image(
            colorFilter = ColorFilter.tint(color),
            imageVector = imageVector,
            contentDescription = contentDescription,
            modifier = Modifier.graphicsLayer {
                alpha = if(enabled) 1f else 0.3f
            }
        )
    }
}
