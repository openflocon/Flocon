package io.github.openflocon.flocondesktop.app.ui.view.topbar.device

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
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
import io.github.openflocon.flocondesktop.app.ui.model.DeviceItemUiModel.Platform
import io.github.openflocon.flocondesktop.app.ui.model.previewDeviceItemUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconDot
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconSurface
import org.jetbrains.compose.resources.stringResource

/** The selected device, shown in the top bar: platform logo, name, connection dot and state. */
@Composable
internal fun TopBarDevice(
    device: DeviceItemUiModel,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    val platformColor = device.platform.color()

    Row(
        modifier = modifier.padding(FloconTheme.spacing.small),
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // the platform logo on a soft square, like the app icon next to it
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(32.dp)
                .clip(FloconTheme.shapes.medium)
                .background(colors.bg300),
        ) {
            FloconIcon(
                modifier = Modifier.size(18.dp),
                imageVector = device.platform.logo(),
                tint = if (device.isActive) platformColor else colors.inkFaint,
            )
        }

        Column {
            Text(
                text = device.deviceName,
                style = FloconTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = if (device.isActive) colors.ink else colors.inkMuted,
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.compact),
            ) {
                ConnectionDot(isActive = device.isActive)
                Text(
                    text = if (device.isActive) {
                        stringResource(Res.string.devices_connected)
                    } else {
                        stringResource(Res.string.devices_disconnected)
                    },
                    color = colors.inkFaint,
                    style = FloconTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

/** A device in the dropdown: connection dot, name, then the check when selected or the delete button. */
@Composable
internal fun TopBarDeviceRow(
    device: DeviceItemUiModel,
    selected: Boolean,
    onClick: () -> Unit,
    deleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(FloconTheme.shapes.medium)
            .then(if (selected) Modifier.background(colors.bg300) else Modifier)
            .clickable(onClick = onClick)
            .padding(horizontal = FloconTheme.spacing.medium, vertical = FloconTheme.spacing.small),
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ConnectionDot(isActive = device.isActive)
        Text(
            text = device.deviceName,
            style = FloconTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = if (device.isActive) colors.ink else colors.inkMuted,
            modifier = Modifier.weight(1f),
        )
        if (selected) {
            FloconIcon(
                imageVector = Icons.Outlined.Check,
                tint = colors.accent,
                modifier = Modifier.size(16.dp),
            )
        } else {
            Box(
                Modifier
                    .clip(FloconTheme.shapes.small)
                    .background(colors.bg300)
                    .clickable(onClick = deleteClick)
                    .padding(FloconTheme.spacing.tiny),
                contentAlignment = Alignment.Center,
            ) {
                FloconIcon(
                    imageVector = Icons.Outlined.Close,
                    tint = colors.inkMuted,
                    modifier = Modifier.size(14.dp),
                )
            }
        }
    }
}

/** The title of a group of devices of the same platform in the dropdown. */
@Composable
internal fun TopBarDeviceGroupHeader(
    platform: Platform,
    count: Int,
    modifier: Modifier = Modifier,
) {
    val color = platform.color()

    Row(
        modifier = modifier.padding(
            start = FloconTheme.spacing.medium,
            end = FloconTheme.spacing.medium,
            top = FloconTheme.spacing.small,
            bottom = FloconTheme.spacing.extraSmall,
        ),
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FloconIcon(
            imageVector = platform.logo(),
            tint = color,
            modifier = Modifier.size(14.dp),
        )
        Text(
            text = platform.label(),
            style = FloconTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = color,
        )
        Text(
            text = count.toString(),
            style = FloconTheme.typography.labelSmall,
            color = FloconTheme.colors.inkFaint,
        )
    }
}

// connected: a plain dot, not connected: a ring
@Composable
private fun ConnectionDot(isActive: Boolean) {
    if (isActive) {
        FloconDot(color = FloconTheme.colors.statusSuccess)
    } else {
        Box(
            Modifier
                .size(6.dp)
                .border(1.5.dp, FloconTheme.colors.inkFaint, CircleShape),
        )
    }
}

@Preview
@Composable
private fun TopBarDevicePreview() {
    FloconTheme {
        FloconSurface {
            Column {
                Platform.entries.forEach { platform ->
                    TopBarDevice(device = previewDeviceItemUiModel(platform = platform, isActive = true))
                    TopBarDevice(device = previewDeviceItemUiModel(platform = platform, isActive = false))
                }
            }
        }
    }
}

@Preview
@Composable
private fun TopBarDeviceRowPreview() {
    FloconTheme {
        FloconSurface {
            Column {
                TopBarDeviceRow(
                    device = previewDeviceItemUiModel(platform = Platform.Android, isActive = true),
                    selected = true,
                    onClick = {},
                    deleteClick = {},
                )
                TopBarDeviceRow(
                    device = previewDeviceItemUiModel(platform = Platform.Android, isActive = false),
                    selected = false,
                    onClick = {},
                    deleteClick = {},
                )
            }
        }
    }
}

@Preview
@Composable
private fun TopBarDeviceGroupHeaderPreview() {
    FloconTheme {
        FloconSurface {
            Column {
                Platform.entries.forEach { TopBarDeviceGroupHeader(platform = it, count = 2) }
            }
        }
    }
}
