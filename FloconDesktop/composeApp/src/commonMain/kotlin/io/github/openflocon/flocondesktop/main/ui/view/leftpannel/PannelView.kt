@file:OptIn(ExperimentalSharedTransitionApi::class)

package io.github.openflocon.flocondesktop.main.ui.view.leftpannel

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun PannelView(
    icon: ImageVector,
    text: String,
    isSelected: Boolean,
    expanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(8.dp)
    val shadow by animateDpAsState(
        targetValue = if (isSelected) 6.dp else 0.dp,
        label = "shadow",
    )
    val color by animateColorAsState(
        targetValue = if (isSelected) FloconTheme.colorPalette.panel else FloconTheme.colorPalette.surface,
        label = "color",
    )

    Row(
        modifier = modifier
            .height(28.dp)
            .shadow(elevation = shadow, shape = shape, clip = true, ambientColor = color, spotColor = color)
            .background(color)
            .clickable(onClick = onClick, interactionSource = null, indication = null)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier
                .size(16.dp),
            imageVector = icon,
            contentDescription = "Description de mon image",
            tint = FloconTheme.colorPalette.onSurface,
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
        PannelView(
            icon = Icons.Outlined.Settings,
            text = "text",
            isSelected = false,
            onClick = {},
            expanded = false,
        )
    }
}

@Composable
@Preview
private fun PannelViewPreview_Selected() {
    FloconTheme {
        PannelView(
            icon = Icons.Outlined.Settings,
            text = "text",
            isSelected = true,
            onClick = {},
            expanded = false,
        )
    }
}
