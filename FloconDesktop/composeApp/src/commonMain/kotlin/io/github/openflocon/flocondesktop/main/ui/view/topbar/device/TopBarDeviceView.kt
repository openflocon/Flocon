package io.github.openflocon.flocondesktop.main.ui.view.topbar.device


import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MobileOff
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.openflocon.flocondesktop.main.ui.model.DeviceItemUiModel
import io.github.openflocon.flocondesktop.main.ui.view.topbar.TopBarSelector
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIcon

@Composable
internal fun TopBarDeviceView(
    device: DeviceItemUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    selected: Boolean = false,
    canDelete: Boolean = false,
    onDelete: () -> Unit = {},
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
            Box(
                modifier = Modifier.padding(horizontal = 4.dp)
                    .graphicsLayer {
                        alpha = if (device.isActive) 1f else 0.4f
                    }
            ) {
                Image(
                    modifier = Modifier.width(20.dp),
                    imageVector = if (device.isActive.not()) {
                        Icons.Filled.MobileOff
                    } else {
                        Icons.Filled.Smartphone
                    },
                    colorFilter = ColorFilter.tint(FloconTheme.colorPalette.onSurface),
                    contentDescription = null,
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = device.deviceName,
                        color = FloconTheme.colorPalette.onPanel,
                        style = FloconTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    )

                    Text(
                        text = if (device.isActive.not()) {
                            "Disconnected"
                        } else "Connected",
                        color = FloconTheme.colorPalette.onPanel,
                        style = FloconTheme.typography.bodySmall.copy(
                            fontSize = 10.sp,
                        ),
                    )
                }
                if (selected)
                    FloconIcon(
                        imageVector = Icons.Outlined.Check,
                        tint = FloconTheme.colorPalette.onPanel,
                    )
                else if (canDelete) {
                    FloconIcon(
                        imageVector = Icons.Outlined.Delete,
                        tint = FloconTheme.colorPalette.onPanel,
                        modifier = Modifier.clickable {
                            onDelete()
                        }
                    )
                }
            }
        }
    }
}
