@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.openflocon.flocondesktop.main.ui.view.topbar.device


import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.main.ui.model.DeviceItemUiModel
import io.github.openflocon.flocondesktop.main.ui.model.DevicesStateUiModel
import io.github.openflocon.flocondesktop.main.ui.view.topbar.TopBarSelector
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconCircularProgressIndicator

@Composable
internal fun TopBarDeviceDropdown(
    state: DevicesStateUiModel,
    onDeviceSelected: (DeviceItemUiModel) -> Unit,
    deleteDevice: (DeviceItemUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = it
        },
        modifier = modifier,
    ) {
        when (state) {
            DevicesStateUiModel.Empty -> Empty()
            DevicesStateUiModel.Loading -> Loading()
            is DevicesStateUiModel.WithDevices -> TopBarDeviceView(
                device = state.deviceSelected,
                onClick = {},
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
            )
        }
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = FloconTheme.colorPalette.panel,
            shadowElevation = 0.dp,
            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
            modifier = Modifier.exposedDropdownSize(),
        ) {
            if (state is DevicesStateUiModel.WithDevices) {
                state.devices.forEach { device ->
                    TopBarDeviceView(
                        device = device,
                        selected = state.deviceSelected.id == device.id,
                        onClick = {
                            onDeviceSelected(device)
                            expanded = false
                        },
                        canDelete = true,
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
            text = "No Devices Found",
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
