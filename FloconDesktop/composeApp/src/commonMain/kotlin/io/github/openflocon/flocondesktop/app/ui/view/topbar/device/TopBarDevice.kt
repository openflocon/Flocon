package io.github.openflocon.flocondesktop.app.ui.view.topbar.device

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DesktopAccessDisabled
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.MobileOff
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.MobileOff
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.devices_connected
import flocondesktop.composeapp.generated.resources.devices_disconnected
import io.github.openflocon.flocondesktop.app.ui.model.DeviceItemUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconDot
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconSurface
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun TopBarDevice(
    device: DeviceItemUiModel,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    deleteClick: (() -> Unit)? = null,
    detailClick: (() -> Unit)? = null,
) {
    val colors = FloconTheme.colors

    Row(
        modifier = modifier.padding(
            start = FloconTheme.spacing.small,
            end = FloconTheme.spacing.extraSmall,
            top = FloconTheme.spacing.small,
            bottom = FloconTheme.spacing.small
        ),
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // the device icon on a soft square, like the app icon next to it
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(32.dp)
                .clip(FloconTheme.shapes.medium)
                .background(colors.bg300),
        ) {
            FloconIcon(
                modifier = Modifier.size(16.dp),
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
                tint = colors.inkFaint,
            )
        }

        Column {
            Text(
                text = device.deviceName,
                style = FloconTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = colors.ink,
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.compact),
            ) {
                // connected: a plain dot, not connected: a ring
                if (device.isActive) {
                    FloconDot(color = colors.statusSuccess)
                } else {
                    Box(
                        Modifier
                            .size(6.dp)
                            .border(1.5.dp, colors.inkFaint, CircleShape)
                    )
                }
                Text(
                    text = if (device.isActive.not()) {
                        stringResource(Res.string.devices_disconnected)
                    } else {
                        stringResource(Res.string.devices_connected)
                    },
                    color = colors.inkFaint,
                    style = FloconTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
        if (detailClick != null) {
            Box(
                Modifier
                    .clip(FloconTheme.shapes.small)
                    .background(colors.bg300)
                    .padding(FloconTheme.spacing.tiny)
                    .clickable(onClick = detailClick),
                contentAlignment = Alignment.Center,
            ) {
                FloconIcon(
                    imageVector = Icons.Outlined.Info,
                    tint = colors.inkMuted,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
        if (!selected && deleteClick != null) {
            Spacer(modifier = Modifier.weight(1f))
            Box(
                Modifier
                    .clip(FloconTheme.shapes.small)
                    .background(colors.bg300)
                    .padding(FloconTheme.spacing.tiny)
                    .clickable(onClick = deleteClick),
                contentAlignment = Alignment.Center,
            ) {
                FloconIcon(
                    imageVector = Icons.Outlined.Close,
                    tint = colors.inkMuted,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
        if (selected) {
            Spacer(modifier = Modifier.weight(1f))
            FloconIcon(
                imageVector = Icons.Outlined.Check,
                tint = colors.accent,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

@Preview
@Composable
private fun TopBarDeviceViewPreview_desktop_enabled() {
    FloconTheme {
        TopBarDevice(
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
            deleteClick = {},
        )
    }
}

@Preview
@Composable
private fun TopBarDeviceViewPreview_desktop_disabled() {
    FloconTheme {
        TopBarDevice(
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
            deleteClick = {},
        )
    }
}

@Preview
@Composable
private fun TopBarDeviceViewPreview_iphone() {
    FloconTheme {
        FloconSurface {
            TopBarDevice(
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
                deleteClick = {},
            )
        }
    }
}

@Preview
@Composable
private fun TopBarDeviceViewPreview_iphone_disabled() {
    FloconTheme {
        FloconSurface {
            TopBarDevice(
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
                deleteClick = {},
            )
        }
    }
}
