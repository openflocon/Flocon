@file:OptIn(ExperimentalMaterial3Api::class)
@file:Suppress("UnusedReceiverParameter")

package io.github.openflocon.flocondesktop.app.ui.view.topbar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.app.ui.model.AppsStateUiModel
import io.github.openflocon.flocondesktop.app.ui.model.DeviceAppUiModel
import io.github.openflocon.flocondesktop.app.ui.model.DeviceItemUiModel
import io.github.openflocon.flocondesktop.app.ui.model.DevicesStateUiModel
import io.github.openflocon.flocondesktop.app.ui.view.topbar.app.TopBarAppDropdown
import io.github.openflocon.flocondesktop.app.ui.view.topbar.device.TopBarDeviceDropdown
import io.github.openflocon.library.designsystem.FloconTheme

@Composable
internal fun TopBarDeviceAndApp(
    devicesState: DevicesStateUiModel,
    appsState: AppsStateUiModel,
    onDeviceSelected: (DeviceItemUiModel) -> Unit,
    deleteDevice: (DeviceItemUiModel) -> Unit,
    onAppSelected: (DeviceAppUiModel) -> Unit,
    deleteApp: (DeviceAppUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    // concentric with the 6dp tiles inside: 12 outer radius - 6 padding
    val shape = RoundedCornerShape(12.dp)

    // device and app are one segmented control
    CompositionLocalProvider(LocalTopBarSelectorJoined provides true) {
        Row(
            modifier = modifier
                .height(IntrinsicSize.Min)
                .clip(shape)
                .background(colors.bg100)
                .border(1.dp, colors.line, shape),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TopBarDeviceDropdown(
                state = devicesState,
                onDeviceSelected = onDeviceSelected,
                deleteDevice = deleteDevice,
            )

            AnimatedVisibility(devicesState is DevicesStateUiModel.WithDevices) {
                Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                    Box(
                        Modifier
                            .fillMaxHeight()
                            .width(1.dp)
                            .background(colors.line)
                    )
                    TopBarAppDropdown(
                        devicesState = devicesState,
                        appsState = appsState,
                        onAppSelected = onAppSelected,
                        deleteApp = deleteApp,
                    )
                }
            }
        }
    }
}
