package io.github.openflocon.flocondesktop.main.ui.view.topbar


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.smartphone
import io.github.openflocon.flocondesktop.main.ui.model.DeviceItemUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIcon
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun TopBarDeviceView(
    device: DeviceItemUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    selected: Boolean = false,
) {
    TopBarSelector(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Image(
                modifier = Modifier.size(24.dp),
                painter = painterResource(Res.drawable.smartphone),
                contentDescription = null,
            )

            Row(
                modifier = Modifier
                    .padding(start = 4.dp, end = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier
                        .graphicsLayer {
                            alpha = if (device.isActive) 1f else 0.4f
                        },
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = device.deviceName,
                        color = FloconTheme.colorPalette.onPanel,
                        style = FloconTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    )
                    if (device.isActive.not()) {
                        Text(
                            text = "Disconnected",
                            color = FloconTheme.colorPalette.onPanel,
                            style = FloconTheme.typography.bodySmall.copy(
                                fontSize = 10.sp,
                            ),
                        )
                    }
                }
                if (selected)
                    FloconIcon(
                        imageVector = Icons.Outlined.Check,
                        tint = FloconTheme.colorPalette.onPanel,
                    )
            }
        }
    }
}
