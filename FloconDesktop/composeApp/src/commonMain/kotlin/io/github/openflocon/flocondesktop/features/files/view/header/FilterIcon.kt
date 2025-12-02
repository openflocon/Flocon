package io.github.openflocon.flocondesktop.features.files.view.header

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

internal val FilterIcon_Height = 32.dp
internal val FilterIcon_Spacing = 2.dp
private val FilterIcon = (FilterIcon_Height - FilterIcon_Spacing) / 2

@Composable
internal fun FilterIcon(
    icon: ImageVector,
    hovered: Boolean,
    sorted: Boolean,
    onClick: () -> Unit,
    shape: Shape,
    textColor: Color,
    alignment: Alignment,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(FilterIcon)
            .graphicsLayer {
                alpha = if (hovered || sorted) 1f else 0f
            }
            .clip(shape)
            .clickable(onClick = onClick),
        contentAlignment = alignment,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.size(FilterIcon),
        )
    }
}
