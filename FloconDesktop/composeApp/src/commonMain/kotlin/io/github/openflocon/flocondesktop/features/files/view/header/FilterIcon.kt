package io.github.openflocon.flocondesktop.features.files.view.header

import io.github.openflocon.library.designsystem.FloconTheme
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
    val filterIconSize = (FloconTheme.sizes.headerHeight - FloconTheme.spacing.tiny) / 2
    Box(
        modifier = modifier
            .size(filterIconSize)
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
            modifier = Modifier.size(filterIconSize),
        )
    }
}
