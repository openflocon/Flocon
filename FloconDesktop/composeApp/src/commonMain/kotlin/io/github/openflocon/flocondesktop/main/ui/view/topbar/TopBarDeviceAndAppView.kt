@file:OptIn(ExperimentalMaterial3Api::class)
@file:Suppress("UnusedReceiverParameter")

package io.github.openflocon.flocondesktop.main.ui.view.topbar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.openflocon.flocondesktop.main.ui.model.AppsStateUiModel
import io.github.openflocon.flocondesktop.main.ui.model.DeviceAppUiModel
import io.github.openflocon.flocondesktop.main.ui.model.DeviceItemUiModel
import io.github.openflocon.flocondesktop.main.ui.model.DevicesStateUiModel
import io.github.openflocon.flocondesktop.main.ui.view.topbar.app.TopBarAppDropdown
import io.github.openflocon.flocondesktop.main.ui.view.topbar.device.TopBarDeviceDropdown
import io.github.openflocon.library.designsystem.FloconTheme

@Composable
internal fun TopBarDeviceAndAppView(
    devicesState: DevicesStateUiModel,
    appsState: AppsStateUiModel,
    onDeviceSelected: (DeviceItemUiModel) -> Unit,
    onAppSelected: (DeviceAppUiModel) -> Unit,
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
        )

        AnimatedVisibility(devicesState is DevicesStateUiModel.WithDevices) {
            TopBarAppDropdown(
                devicesState = devicesState,
                appsState = appsState,
                onAppSelected = onAppSelected,
            )
        }
    }
}
