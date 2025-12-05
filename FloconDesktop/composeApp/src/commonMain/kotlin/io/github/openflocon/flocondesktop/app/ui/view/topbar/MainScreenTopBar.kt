package io.github.openflocon.flocondesktop.app.ui.view.topbar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.app_icon_small
import io.github.openflocon.flocondesktop.app.ui.model.AppsStateUiModel
import io.github.openflocon.flocondesktop.app.ui.model.DeviceAppUiModel
import io.github.openflocon.flocondesktop.app.ui.model.DeviceItemUiModel
import io.github.openflocon.flocondesktop.app.ui.model.DevicesStateUiModel
import io.github.openflocon.flocondesktop.app.ui.model.RecordVideoStateUiModel
import io.github.openflocon.flocondesktop.app.ui.view.topbar.actions.TopBarActions
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.resources.painterResource

val TopBarHeight = 48.dp

@Composable
fun MainScreenTopBar(
    modifier: Modifier = Modifier,
    devicesState: DevicesStateUiModel,
    appsState: AppsStateUiModel,
    onDeviceSelected: (DeviceItemUiModel) -> Unit,
    deleteDevice: (DeviceItemUiModel) -> Unit,
    onAppSelected: (DeviceAppUiModel) -> Unit,
    deleteApp: (DeviceAppUiModel) -> Unit,
    onTakeScreenshotClicked: () -> Unit,
    recordState: RecordVideoStateUiModel,
    onRecordClicked: () -> Unit,
    onRestartClicked: () -> Unit,
    onClickDetail: (DeviceItemUiModel) -> Unit,
) {
    Row(
        modifier = modifier
            .background(FloconTheme.colorPalette.surface)
            .padding(vertical = 8.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Title()
        Spacer(modifier = Modifier.width(18.dp))
        TopBarDeviceAndAppView(
            devicesState = devicesState,
            appsState = appsState,
            onDeviceSelected = onDeviceSelected,
            onAppSelected = onAppSelected,
            deleteDevice = deleteDevice,
            deleteApp = deleteApp,
            onClickDetail = onClickDetail
        )
        Spacer(modifier = Modifier.weight(1f))
        TopBarActions(
            onTakeScreenshotClicked = onTakeScreenshotClicked,
            recordState = recordState,
            onRecordClicked = onRecordClicked,
            onRestartClicked = onRestartClicked,
            devicesState = devicesState,
        )
    }
}

@Composable
private fun Title(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(8.dp)),
            painter = painterResource(Res.drawable.app_icon_small),
            contentDescription = "Description de mon image",
        )

        Text(
            text = "Flocon",
            style = FloconTheme.typography.titleSmall.copy(
                fontSize = 18.sp,
                color = FloconTheme.colorPalette.onSurface,
                fontWeight = FontWeight.SemiBold,
            ),
        )
    }
}
