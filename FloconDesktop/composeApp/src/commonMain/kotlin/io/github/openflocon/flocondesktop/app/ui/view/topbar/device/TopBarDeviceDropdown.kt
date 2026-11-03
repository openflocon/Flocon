package io.github.openflocon.flocondesktop.app.ui.view.topbar.device

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.no_devices_found
import io.github.openflocon.flocondesktop.app.ui.model.DeviceItemUiModel
import io.github.openflocon.flocondesktop.app.ui.model.DevicesStateUiModel
import io.github.openflocon.flocondesktop.app.ui.view.topbar.TopBarSelector
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconCircularProgressIndicator
import io.github.openflocon.library.designsystem.components.FloconDropdownMenu
import io.github.openflocon.library.designsystem.components.FloconHorizontalDivider
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun TopBarDeviceDropdown(
    state: DevicesStateUiModel,
    onDeviceSelected: (DeviceItemUiModel) -> Unit,
    onOpenInNewWindow: (DeviceItemUiModel) -> Unit,
    deleteDevice: (DeviceItemUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    when (state) {
        DevicesStateUiModel.Empty -> Empty()

        DevicesStateUiModel.Loading -> Loading()

        is DevicesStateUiModel.WithDevices -> FloconDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            onExpandRequest = { expanded = true },
            modifier = modifier,
            anchorContent = {
                TopBarSelector(
                    onClick = { expanded = true },
                    modifier = Modifier.width(FloconTheme.sizes.deviceDropdownWidth),
                ) {
                    TopBarDevice(device = state.deviceSelected)
                }
            },
        ) {
            state.groups.forEachIndexed { index, group ->
                if (index > 0) {
                    FloconHorizontalDivider(
                        modifier = Modifier.padding(vertical = FloconTheme.spacing.extraSmall),
                    )
                }
                TopBarDeviceGroupHeader(
                    platform = group.platform,
                    count = group.devices.size,
                    modifier = Modifier.width(FloconTheme.sizes.deviceDropdownWidth),
                )
                group.devices.forEach { device ->
                    TopBarDeviceRow(
                        modifier = Modifier.width(FloconTheme.sizes.deviceDropdownWidth),
                        device = device,
                        selected = state.deviceSelected.id == device.id,
                        onClick = {
                            onDeviceSelected(device)
                            expanded = false
                        },
                        onOpenInNewWindow = {
                            onOpenInNewWindow(device)
                            expanded = false
                        },
                        deleteClick = {
                            deleteDevice(device)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun Empty() {
    TopBarSelector(
        onClick = {},
        modifier = Modifier
            .height(48.dp)
            .width(FloconTheme.sizes.deviceDropdownWidth)
    ) {
        Text(
            text = stringResource(Res.string.no_devices_found),
            modifier = Modifier
                .fillMaxHeight()
                .padding(
                    vertical = FloconTheme.spacing.extraSmall,
                    horizontal = FloconTheme.spacing.medium
                ),
            style = FloconTheme.typography.bodyMedium,
            color = FloconTheme.colors.ink,
        )
    }
}

@Composable
private fun Loading() {
    TopBarSelector(
        onClick = {},
        modifier = Modifier.width(FloconTheme.sizes.deviceDropdownWidth),
    ) {
        FloconCircularProgressIndicator()
    }
}
