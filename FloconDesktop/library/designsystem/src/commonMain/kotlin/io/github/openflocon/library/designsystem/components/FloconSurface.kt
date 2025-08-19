package io.github.openflocon.library.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.theme.contentColorFor

@Composable
fun FloconSurface(
    modifier: Modifier = Modifier,
    color: Color = FloconTheme.colorPalette.surface,
    shape: Shape = RectangleShape,
    contentColor: Color = FloconTheme.colorPalette.contentColorFor(color),
    tonalElevation: Dp = 0.dp,
    shadowElevation: Dp = 0.dp,
    border: BorderStroke? = null,
    content: @Composable () -> Unit
) {
    Surface(
        color = color,
        contentColor = contentColor,
        border = border,
        shadowElevation = shadowElevation,
        tonalElevation = tonalElevation,
        shape = shape,
        content = content,
        modifier = modifier
    )
}

@Composable
fun FloconSurface(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = FloconTheme.colorPalette.surface,
    enabled: Boolean = true,
    shape: Shape = RectangleShape,
    contentColor: Color = FloconTheme.colorPalette.contentColorFor(color),
    tonalElevation: Dp = 0.dp,
    shadowElevation: Dp = 0.dp,
    border: BorderStroke? = null,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable () -> Unit
) {
    Surface(
        color = color,
        interactionSource = interactionSource,
        contentColor = contentColor,
        onClick = onClick,
        border = border,
        shadowElevation = shadowElevation,
        tonalElevation = tonalElevation,
        enabled = enabled,
        shape = shape,
        content = content,
        modifier = modifier
    )
}
