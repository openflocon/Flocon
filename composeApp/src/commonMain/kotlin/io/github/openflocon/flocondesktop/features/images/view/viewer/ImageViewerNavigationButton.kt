package io.github.openflocon.flocondesktop.features.images.view.viewer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import io.github.openflocon.library.designsystem.FloconTheme

private val NavigationButtonSize = 48.dp
private val NavigationButtonAlpha = 0.7f

@Composable
internal fun ImageViewerNavigationButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(NavigationButtonSize)
            .clip(CircleShape)
            .background(FloconTheme.colors.floating.copy(alpha = NavigationButtonAlpha))
            .clickable(onClick = onClick)
            .pointerHoverIcon(PointerIcon.Hand),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = FloconTheme.colors.ink,
        )
    }
}
