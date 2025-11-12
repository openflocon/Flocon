@file:OptIn(ExperimentalMaterial3Api::class)
@file:Suppress("UnusedReceiverParameter")

package io.github.openflocon.flocondesktop.app.ui.view.topbar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.app.ui.model.AppsStateUiModel
import io.github.openflocon.flocondesktop.app.ui.model.DeviceAppUiModel
import io.github.openflocon.flocondesktop.app.ui.model.DeviceItemUiModel
import io.github.openflocon.flocondesktop.app.ui.model.DevicesStateUiModel
import io.github.openflocon.flocondesktop.app.ui.view.topbar.app.TopBarAppDropdown
import io.github.openflocon.flocondesktop.app.ui.view.topbar.device.TopBarDeviceDropdown

@Composable
internal fun TopBarDeviceAndAppView(
    devicesState: DevicesStateUiModel,
    appsState: AppsStateUiModel,
    onDeviceSelected: (DeviceItemUiModel) -> Unit,
    deleteDevice: (DeviceItemUiModel) -> Unit,
    onAppSelected: (DeviceAppUiModel) -> Unit,
    deleteApp: (DeviceAppUiModel) -> Unit,
    onClickDetail: (DeviceItemUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TopBarDeviceDropdown(
            state = devicesState,
            onDeviceSelected = onDeviceSelected,
            deleteDevice = deleteDevice,
            onClickDetail = onClickDetail
        )

        AnimatedVisibility(devicesState is DevicesStateUiModel.WithDevices) {
            TopBarAppDropdown(
                devicesState = devicesState,
                appsState = appsState,
                onAppSelected = onAppSelected,
                deleteApp = deleteApp,
            )
        }
    }
}
