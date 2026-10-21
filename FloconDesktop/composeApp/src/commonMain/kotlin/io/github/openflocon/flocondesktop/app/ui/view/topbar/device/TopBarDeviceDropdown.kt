package io.github.openflocon.flocondesktop.app.ui.view.topbar.device

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import org.jetbrains.compose.resources.stringResource

private val MenuWidth = 220.dp

@Composable
internal fun TopBarDeviceDropdown(
    state: DevicesStateUiModel,
    onDeviceSelected: (DeviceItemUiModel) -> Unit,
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
                    modifier = Modifier.width(MenuWidth * FloconTheme.scale),
                ) {
                    TopBarDeviceView(device = state.deviceSelected)
                }
            },
        ) {
            state.devices.forEach { device ->
                val isSelected = state.deviceSelected.id == device.id

                TopBarDeviceView(
                    modifier = Modifier
                        .width(MenuWidth * FloconTheme.scale)
                        .clickable {
                            onDeviceSelected(device)
                            expanded = false
                        }
                        .then(
                            if (isSelected) {
                                Modifier.background(color = FloconTheme.colors.bg300)
                            } else {
                                Modifier
                            }
                        ),
                    device = device,
                    selected = isSelected,
                    deleteClick = {
                        deleteDevice(device)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun Empty() {
    TopBarSelector(
        onClick = {},
        modifier = Modifier.width(MenuWidth * FloconTheme.scale),
    ) {
        Text(
            text = stringResource(Res.string.no_devices_found),
            modifier = Modifier.padding(
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
        modifier = Modifier.width(MenuWidth * FloconTheme.scale),
    ) {
        FloconCircularProgressIndicator()
    }
}
