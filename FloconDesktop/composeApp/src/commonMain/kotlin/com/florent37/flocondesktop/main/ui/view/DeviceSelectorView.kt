package com.florent37.flocondesktop.main.ui.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.florent37.flocondesktop.common.ui.FloconColorScheme
import com.florent37.flocondesktop.common.ui.FloconColors
import com.florent37.flocondesktop.common.ui.FloconTheme
import com.florent37.flocondesktop.main.ui.model.DeviceItemUiModel
import com.florent37.flocondesktop.main.ui.model.DevicesStateUiModel
import com.florent37.flocondesktop.main.ui.model.previewDeviceItemUiModelPreview
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.smartphone
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun DeviceSelectorView(
    devicesState: DevicesStateUiModel,
    onDeviceSelected: (DeviceItemUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(12.dp)

    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .shadow(elevation = 4.dp, shape = shape, clip = true)
            .background(color = FloconColors.pannel)
            .clickable {
                expanded = true
            },
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = 8.dp,
                vertical = 4.dp,
            ),
        ) {
            when (devicesState) {
                DevicesStateUiModel.Loading -> {
                    // hide
                }

                DevicesStateUiModel.Empty -> {
                    Text(
                        text = "No Devices Found",
                        modifier = Modifier.padding(vertical = 4.dp, horizontal = 12.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }

                is DevicesStateUiModel.WithDevices -> {
                    DeviceView(
                        device = devicesState.selected,
                    )

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        devicesState.devices.forEach { device ->
                            DeviceView(
                                device = device,
                                modifier = Modifier.fillMaxWidth().clickable {
                                    onDeviceSelected(device)
                                    expanded = false // Close the dropdown after selection
                                },
                            )
                            // DropdownMenuItem(
                            //    text = {
                            //        DeviceView(
                            //            device = device,
                            //        )
                            //    },
                            //    onClick = {
                            //        onDeviceSelected(device)
                            //        expanded = false // Close the dropdown after selection
                            //    },
                            //    modifier = Modifier.fillMaxWidth(),
                            // )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DeviceView(
    device: DeviceItemUiModel,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier = Modifier.size(24.dp),
            painter = painterResource(Res.drawable.smartphone),
            contentDescription = null,
        )
        Column(modifier = Modifier.padding(start = 4.dp)) {
            Text(
                text = device.deviceName, // Device Name
                color = FloconColorScheme.onSurface,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            )
            Text(
                text = device.appName,
                color = FloconColorScheme.onSurface.copy(alpha = 0.5f),
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Thin),
            )
            Text(
                text = device.appPackageName,
                color = FloconColorScheme.onSurface.copy(alpha = 0.5f),
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Thin),
            )
        }
    }
}

@Preview
@Composable
private fun DeviceViewPreview() {
    FloconTheme {
        DeviceView(
            device = previewDeviceItemUiModelPreview(),
        )
    }
}
