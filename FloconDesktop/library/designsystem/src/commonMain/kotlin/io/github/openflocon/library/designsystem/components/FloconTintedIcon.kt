package io.github.openflocon.library.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.openflocon.library.designsystem.FloconTheme

private const val TileAlpha = 0.16f

/**
 * An [imageVector] tinted with [color], on a rounded tile of the same [color] (translucent).
 * Tells kinds of items apart at a glance, e.g. the storage of a preference in a selector.
 */
@Composable
fun FloconTintedIcon(
    imageVector: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    size: Dp = 22.dp,
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(FloconTheme.shapes.small)
            .background(color.copy(alpha = TileAlpha)),
        contentAlignment = Alignment.Center,
    ) {
        FloconIcon(
            imageVector = imageVector,
            tint = color,
            modifier = Modifier.size(size * 0.6f),
        )
    }
}

@Preview
@Composable
private fun FloconTintedIconPreview() {
    FloconTheme {
        FloconTintedIcon(
            imageVector = Icons.Outlined.Storage,
            color = FloconTheme.colors.catDatabase,
        )
    }
}
