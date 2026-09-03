package io.github.openflocon.flocondesktop.app.ui.view.topbar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowCircleUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
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
import io.github.openflocon.flocondesktop.app.version.VersionCheckerViewModel
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.resources.painterResource

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
    updateChip: VersionCheckerViewModel.UpdateChipUiModel? = null,
    onUpdateChipClicked: (VersionCheckerViewModel.UpdateChipUiModel) -> Unit = {},
) {
    Row(
        modifier = modifier
            .background(FloconTheme.colorPalette.surface)
            .padding(vertical = 8.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Title(
            updateChip = updateChip,
            onUpdateChipClicked = onUpdateChipClicked,
        )
        Spacer(modifier = Modifier.width(18.dp))
        TopBarDeviceAndAppView(
            devicesState = devicesState,
            appsState = appsState,
            onDeviceSelected = onDeviceSelected,
            onAppSelected = onAppSelected,
            deleteDevice = deleteDevice,
            deleteApp = deleteApp,
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
private fun TopBarUpdateChip(
    uiModel: VersionCheckerViewModel.UpdateChipUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(FloconTheme.colorPalette.accent.copy(alpha = 0.2f))
            .border(
                width = 1.dp,
                color = FloconTheme.colorPalette.onAccent.copy(alpha = 0.4f),
                shape = RoundedCornerShape(4.dp),
            )
            .clickable(onClick = onClick)
            .pointerHoverIcon(PointerIcon.Hand)
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(
            imageVector = Icons.Outlined.ArrowCircleUp,
            contentDescription = null,
            tint = FloconTheme.colorPalette.onAccent,
            modifier = Modifier.size(12.dp),
        )
        Text(
            text = uiModel.text,
            color = FloconTheme.colorPalette.onAccent,
            style = FloconTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
            ),
        )
    }
}

@Composable
private fun Title(
    modifier: Modifier = Modifier,
    updateChip: VersionCheckerViewModel.UpdateChipUiModel? = null,
    onUpdateChipClicked: (VersionCheckerViewModel.UpdateChipUiModel) -> Unit = {},
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

        Column(
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "Flocon",
                style = FloconTheme.typography.titleSmall.copy(
                    fontSize = 18.sp,
                    color = FloconTheme.colorPalette.onSurface,
                    fontWeight = FontWeight.SemiBold,
                ),
            )

            if (updateChip != null) {
                Spacer(modifier = Modifier.height(2.dp))
                TopBarUpdateChip(
                    uiModel = updateChip,
                    onClick = { onUpdateChipClicked(updateChip) },
                )
            }
        }
    }
}
