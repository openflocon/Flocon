@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.openflocon.flocondesktop.app.ui.view.topbar.device

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MenuAnchorType
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
import io.github.openflocon.library.designsystem.components.FloconExposedDropdownMenu
import io.github.openflocon.library.designsystem.components.FloconExposedDropdownMenuBox
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun TopBarDeviceDropdown(
    state: DevicesStateUiModel,
    onDeviceSelected: (DeviceItemUiModel) -> Unit,
    deleteDevice: (DeviceItemUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    FloconExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = it
        },
        modifier = modifier,
    ) {
        when (state) {
            DevicesStateUiModel.Empty -> Empty()
            DevicesStateUiModel.Loading -> Loading()
            is DevicesStateUiModel.WithDevices -> TopBarSelector(
                onClick = {
                    expanded = true
                },
                modifier = modifier,
            ) {
                TopBarDeviceView(
                    device = state.deviceSelected,
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
                )
            }
        }
        FloconExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.exposedDropdownSize(matchAnchorWidth = false),
        ) {
            if (state is DevicesStateUiModel.WithDevices) {
                state.devices.forEach { device ->
                    TopBarDeviceView(
                        modifier = Modifier.fillMaxWidth(),
                        device = device,
                        selected = state.deviceSelected.id == device.id,
                        onClick = {
                            onDeviceSelected(device)
                            expanded = false
                        },
                        onDelete = {
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
    ) {
        Text(
            text = stringResource(Res.string.no_devices_found),
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 12.dp),
            style = FloconTheme.typography.bodyMedium,
            color = FloconTheme.colorPalette.onSurface,
        )
    }
}

@Composable
private fun Loading() {
    TopBarSelector(
        onClick = {},
    ) {
        FloconCircularProgressIndicator()
    }
}
