package io.github.openflocon.library.designsystem.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

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
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(4.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(all = 8.dp),
        content = content
    )
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
            .clickable(enabled = enabled, onClick = onClick),
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
