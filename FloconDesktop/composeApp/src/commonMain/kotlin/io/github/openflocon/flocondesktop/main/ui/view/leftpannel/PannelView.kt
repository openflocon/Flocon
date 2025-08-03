package io.github.openflocon.flocondesktop.main.ui.view.leftpannel

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.settings
import io.github.openflocon.flocondesktop.common.ui.FloconColorScheme
import io.github.openflocon.flocondesktop.common.ui.FloconColors
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun PannelView(
    icon: DrawableResource,
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(8.dp)
    Row(
        modifier = modifier
            .then(
                if (isSelected)
                    Modifier
                        .shadow(elevation = 6.dp, shape = shape, clip = true)
                        .background(FloconColors.pannel)
                else Modifier,
            )
            .clickable(onClick = onClick, interactionSource = null, indication = null)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier = Modifier.size(16.dp),
            painter = painterResource(icon),
            contentDescription = "Description de mon image",
            colorFilter = ColorFilter.tint(
                color = FloconColorScheme.onSurface,
            ),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            color = FloconColorScheme.onSurface,
            style = FloconTheme.typography.bodyMedium,
        )
    }
}

@Composable
@Preview
private fun PannelViewPreview() {
    FloconTheme {
        PannelView(
            icon = Res.drawable.settings,
            text = "text",
            isSelected = false,
            onClick = {},
        )
    }
}

@Composable
@Preview
private fun PannelViewPreview_Selected() {
    FloconTheme {
        PannelView(
            icon = Res.drawable.settings,
            text = "text",
            isSelected = true,
            onClick = {},
        )
    }
}
