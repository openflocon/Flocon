package io.github.openflocon.flocondesktop.main.ui.view.topbar


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import io.github.openflocon.library.designsystem.FloconTheme

@Composable
internal fun TopBarSelector(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(10.dp),
    contentPadding: PaddingValues = PaddingValues(horizontal = 4.dp, vertical = 4.dp),
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier
            .then(
                Modifier
                    .clip(shape)
                    .background(Color.Black.copy(alpha = 0.1f))
                    .clickable(enabled = enabled, onClick = onClick)
                    .padding(contentPadding),
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        content()
        Spacer(modifier = Modifier.width(4.dp))
        Image(
            imageVector = Icons.Outlined.KeyboardArrowDown,
            contentDescription = "",
            modifier = Modifier.width(16.dp),
            colorFilter = ColorFilter.tint(FloconTheme.colorPalette.onSurface)
        )
    }
}
