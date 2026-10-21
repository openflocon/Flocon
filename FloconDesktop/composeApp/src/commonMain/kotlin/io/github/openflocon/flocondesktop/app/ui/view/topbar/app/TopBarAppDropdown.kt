package io.github.openflocon.flocondesktop.app.ui.view.topbar.app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.general_select
import io.github.openflocon.flocondesktop.app.ui.model.AppsStateUiModel
import io.github.openflocon.flocondesktop.app.ui.model.DeviceAppUiModel
import io.github.openflocon.flocondesktop.app.ui.model.DevicesStateUiModel
import io.github.openflocon.flocondesktop.app.ui.view.topbar.TopBarSelector
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconDropdownMenu
import org.jetbrains.compose.resources.stringResource

private val MenuWidth = 300.dp

@Composable
internal fun TopBarAppDropdown(
    devicesState: DevicesStateUiModel,
    appsState: AppsStateUiModel,
    onAppSelected: (DeviceAppUiModel) -> Unit,
    deleteApp: (DeviceAppUiModel) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    if (devicesState is DevicesStateUiModel.WithDevices) {
        FloconDropdownMenu(
            expanded = expanded && appsState is AppsStateUiModel.WithApps,
            onDismissRequest = { expanded = false },
            onExpandRequest = { expanded = true },
            anchorContent = {
                appsState.appSelected?.let {
                    TopBarSelector(
                        onClick = { expanded = true },
                        modifier = Modifier.width(MenuWidth * FloconTheme.scale),
                    ) {
                        TopBarAppView(
                            deviceApp = it,
                            platform = devicesState.deviceSelected.platform,
                        )
                    }
                } ?: run {
                    TopBarSelector(
                        onClick = { expanded = true },
                    ) {
                        Text(text = stringResource(Res.string.general_select))
                    }
                }
            },
        ) {
            if (appsState is AppsStateUiModel.WithApps) {
                appsState.apps
                    .fastForEach { app ->
                        val isSelected = appsState.appSelected?.packageName == app.packageName

                        TopBarAppView(
                            deviceApp = app,
                            platform = devicesState.deviceSelected.platform,
                            selected = isSelected,
                            deleteClick = {
                                deleteApp(app)
                                expanded = false
                            },
                            modifier = Modifier
                                .width(MenuWidth * FloconTheme.scale)
                                .clickable {
                                    onAppSelected(app)
                                    expanded = false
                                }
                                .then(
                                    if (isSelected) {
                                        Modifier.background(color = FloconTheme.colors.bg300)
                                    } else {
                                        Modifier
                                    }
                                ),
                        )
                    }
            }
        }
    }
}
