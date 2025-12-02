package io.github.openflocon.flocondesktop.app.ui.view.topbar.device

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DesktopAccessDisabled
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.MobileOff
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.MobileOff
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.openflocon.flocondesktop.app.ui.model.DeviceItemUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconSurface

@Composable
internal fun TopBarDeviceView(
    device: DeviceItemUiModel,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    selected: Boolean = false,
    onDelete: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .then(
                if (onClick != null)
                    Modifier.clickable(onClick = onClick)
                else
                    Modifier,
            )
            .padding(horizontal = 8.dp, 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .graphicsLayer {
                    alpha = if (device.isActive) 1f else 0.4f
                },
        ) {
            Image(
                modifier = Modifier.width(20.dp),
                imageVector = when (device.platform) {
                    DeviceItemUiModel.Platform.Android -> if (device.isActive.not()) {
                        Icons.Filled.MobileOff
                    } else {
                        Icons.Filled.Smartphone
                    }

                    DeviceItemUiModel.Platform.Desktop -> if (device.isActive.not()) {
                        Icons.Filled.DesktopAccessDisabled
                    } else {
                        Icons.Filled.DesktopWindows
                    }

                    DeviceItemUiModel.Platform.ios -> if (device.isActive.not()) {
                        Icons.Outlined.MobileOff
                    } else {
                        Icons.Filled.PhoneIphone
                    }

                    DeviceItemUiModel.Platform.Unknown -> if (device.isActive.not()) {
                        Icons.Filled.MobileOff
                    } else {
                        Icons.Filled.Smartphone
                    }
                },
                colorFilter = ColorFilter.tint(FloconTheme.colorPalette.onPrimary),
                contentDescription = null,
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = device.deviceName,
                    color = FloconTheme.colorPalette.onPrimary,
                    style = FloconTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                )

                Text(
                    text = if (device.isActive.not()) {
                        "Disconnected"
                    } else "Connected",
                    color = FloconTheme.colorPalette.onPrimary,
                    style = FloconTheme.typography.bodySmall.copy(
                        fontSize = 10.sp,
                    ),
                )
            }
            if (!selected && onDelete != null) {
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    Modifier.clip(RoundedCornerShape(4.dp))
                        .background(
                            Color.White.copy(alpha = 0.8f),
                        ).padding(2.dp).clickable {
                            onDelete()
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    FloconIcon(
                        imageVector = Icons.Outlined.Close,
                        tint = FloconTheme.colorPalette.primary,
                        modifier = Modifier.size(14.dp),
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun TopBarDeviceViewPreview_desktop_enabled() {
    FloconTheme {
        TopBarDeviceView(
            device = DeviceItemUiModel(
                deviceName = "Desktop",
                platform = DeviceItemUiModel.Platform.Desktop,
                isActive = true,
                id = "123",
                canScreenshot = true,
                canScreenRecord = true,
                canRestart = true,
            ),
            selected = false,
            onDelete = {},
            onClick = {},
        )
    }
}

@Preview
@Composable
private fun TopBarDeviceViewPreview_desktop_disabled() {
    FloconTheme {
        TopBarDeviceView(
            device = DeviceItemUiModel(
                deviceName = "Desktop",
                platform = DeviceItemUiModel.Platform.Desktop,
                isActive = false,
                id = "123",
                canScreenshot = true,
                canScreenRecord = true,
                canRestart = true,
            ),
            selected = false,
            onDelete = {},
            onClick = {},
        )
    }
}

@Preview
@Composable
private fun TopBarDeviceViewPreview_iphone() {
    FloconTheme {
        FloconSurface {
            TopBarDeviceView(
                device = DeviceItemUiModel(
                    deviceName = "Desktop",
                    platform = DeviceItemUiModel.Platform.ios,
                    isActive = true,
                    id = "123",
                    canScreenshot = true,
                    canScreenRecord = true,
                    canRestart = true,
                ),
                selected = false,
                onDelete = {},
                onClick = {},
            )
        }
    }
}

@Preview
@Composable
private fun TopBarDeviceViewPreview_iphone_disabled() {
    FloconTheme {
        FloconSurface {
            TopBarDeviceView(
                device = DeviceItemUiModel(
                    deviceName = "Desktop",
                    platform = DeviceItemUiModel.Platform.ios,
                    isActive = false,
                    id = "123",
                    canScreenshot = true,
                    canScreenRecord = true,
                    canRestart = true,
                ),
                selected = false,
                onDelete = {},
                onClick = {},
            )
        }
    }
}
