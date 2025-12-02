@file:OptIn(ExperimentalMaterial3Api::class)

package io.github.openflocon.flocondesktop.app.ui.view.topbar.app

import androidx.compose.foundation.clickable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.util.fastForEach
import io.github.openflocon.flocondesktop.app.ui.model.AppsStateUiModel
import io.github.openflocon.flocondesktop.app.ui.model.DeviceAppUiModel
import io.github.openflocon.flocondesktop.app.ui.model.DevicesStateUiModel
import io.github.openflocon.flocondesktop.app.ui.view.topbar.TopBarSelector
import io.github.openflocon.library.designsystem.components.FloconExposedDropdownMenu
import io.github.openflocon.library.designsystem.components.FloconExposedDropdownMenuBox

@Composable
internal fun TopBarAppDropdown(
    devicesState: DevicesStateUiModel,
    appsState: AppsStateUiModel,
    onAppSelected: (DeviceAppUiModel) -> Unit,
    deleteApp: (DeviceAppUiModel) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    if (devicesState is DevicesStateUiModel.WithDevices) {
        FloconExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = false },
        ) {
            val modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable)

            appsState.appSelected?.let {
                TopBarSelector(
                    onClick = { expanded = true },
                ) {
                    TopBarAppView(
                        deviceApp = it,
                        modifier = modifier,
                        platform = devicesState.deviceSelected.platform,
                    )
                }
            } ?: run {
                TopBarSelector(
                    onClick = { expanded = true },
                ) {
                    Text(
                        text = "Select",
                        modifier = modifier,
                    )
                }
            }

            when (appsState) {
                AppsStateUiModel.Empty,
                AppsStateUiModel.Loading -> {
                    // no op
                }

                is AppsStateUiModel.WithApps -> {
                    FloconExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.exposedDropdownSize()
                    ) {
                        appsState.apps
                            .fastForEach { app ->
                                TopBarAppView(
                                    deviceApp = app,
                                    platform = devicesState.deviceSelected.platform,
                                    selected = appsState.appSelected?.packageName == app.packageName,
                                    deleteClick = {
                                        deleteApp(app)
                                        expanded = false
                                    },
                                    modifier = Modifier.clickable {
                                        onAppSelected(app)
                                        expanded = false
                                    }
                                )
                            }
                    }
                }
            }
        }
    }
}
