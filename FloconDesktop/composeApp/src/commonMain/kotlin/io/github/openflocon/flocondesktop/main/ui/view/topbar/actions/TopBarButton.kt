package io.github.openflocon.flocondesktop.main.ui.view.topbar.actions

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.openflocon.library.designsystem.FloconTheme

@Composable
internal fun TopBarButton(
    imageVector: ImageVector,
    title: String,
    contentDescription: String,
    isEnabled: Boolean,
    onClicked: () -> Unit,
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Black.copy(alpha = 0.1f))
            .then(
                if (isEnabled) {
                Modifier.clickable {
                    onClicked()
                }
            } else {
                Modifier.alpha(0.4f)
            })
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            imageVector = imageVector,
            modifier = Modifier.size(18.dp),
            contentDescription = contentDescription,
            colorFilter = ColorFilter.tint(FloconTheme.colorPalette.onSurface)
        )
        Text(
            text = title,
            style = FloconTheme.typography.bodySmall.copy(
                fontSize = 10.sp,
            ),
            color = FloconTheme.colorPalette.onSurface
        )
    }
}
