package io.github.openflocon.flocondesktop.app.ui.view.topbar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowCircleUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.app_logo_content_description
import flocondesktop.composeapp.generated.resources.app_name
import io.github.openflocon.flocondesktop.BuildConfig
import io.github.openflocon.flocondesktop.app.ui.model.AppsStateUiModel
import io.github.openflocon.flocondesktop.app.ui.model.DeviceAppUiModel
import io.github.openflocon.flocondesktop.app.ui.model.DeviceItemUiModel
import io.github.openflocon.flocondesktop.app.ui.model.DevicesStateUiModel
import io.github.openflocon.flocondesktop.app.ui.model.RecordVideoStateUiModel
import io.github.openflocon.flocondesktop.app.ui.view.topbar.actions.TopBarActions
import io.github.openflocon.flocondesktop.app.ui.view.topbar.actions.TopBarConnectButton
import io.github.openflocon.flocondesktop.app.version.VersionCheckerViewModel
import io.github.openflocon.flocondesktop.features.connect.view.ConnectDialog
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.floconIsland
import org.jetbrains.compose.resources.stringResource

@Composable
fun MainScreenTopBar(
    devicesState: DevicesStateUiModel,
    appsState: AppsStateUiModel,
    onDeviceSelected: (DeviceItemUiModel) -> Unit,
    deleteDevice: (DeviceItemUiModel) -> Unit,
    onAppSelected: (DeviceAppUiModel) -> Unit,
    deleteApp: (DeviceAppUiModel) -> Unit,
    onTakeScreenshotClicked: () -> Unit,
    recordState: RecordVideoStateUiModel,
    onRecordClicked: () -> Unit,
    onOpenFolderClicked: () -> Unit,
    onClickDetail: (DeviceItemUiModel) -> Unit,
    modifier: Modifier = Modifier,
    sdkUpdate: VersionCheckerViewModel.SdkUpdateUiModel? = null,
    onSdkUpdateIgnored: (VersionCheckerViewModel.SdkUpdateUiModel) -> Unit = {},
) {
    var showConnectDialog by remember { mutableStateOf(false) }
    if (showConnectDialog) {
        ConnectDialog(onDismissRequest = { showConnectDialog = false })
    }

    Row(
        modifier = modifier
            .floconIsland(
                Brush.horizontalGradient(
                    0.4f to FloconTheme.colors.islandTopBar,
                    1f to FloconTheme.colors.islandTopBarTint,
                ),
            )
            .padding(vertical = TopBarVerticalPadding, horizontal = FloconTheme.spacing.large),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.medium),
    ) {
        Title(
            modifier = Modifier.padding(end = FloconTheme.spacing.medium),
        )
        TopBarDeviceAndApp(
            devicesState = devicesState,
            appsState = appsState,
            onDeviceSelected = onDeviceSelected,
            onAppSelected = onAppSelected,
            deleteDevice = deleteDevice,
            deleteApp = deleteApp,
        )
        if (sdkUpdate != null) {
            SdkUpdateBadge(
                update = sdkUpdate,
                onIgnore = onSdkUpdateIgnored,
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        TopBarActions(
            onTakeScreenshotClicked = onTakeScreenshotClicked,
            recordState = recordState,
            onRecordClicked = onRecordClicked,
            onOpenFolderClicked = onOpenFolderClicked,
            devicesState = devicesState,
            onDeviceInfoClicked = { devicesState.deviceSelected?.let(onClickDetail) },
        )
        TopBarConnectButton(onClicked = { showConnectDialog = true })
    }
}

@Composable
private fun Title(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AnimatedAppLogo(
            contentDescription = stringResource(Res.string.app_logo_content_description),
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(10.dp)),
        )

        Column(
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = stringResource(Res.string.app_name),
                style = FloconTheme.typography.titleMedium.copy(
                    color = FloconTheme.colors.ink,
                    fontSize = 15.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.01).em,
                ),
            )
            Text(
                text = "v${BuildConfig.APP_VERSION}",
                style = FloconTheme.typography.labelSmall.copy(
                    color = FloconTheme.colors.inkFaint,
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily.Monospace,
                ),
            )
        }
    }
}

private val TopBarVerticalPadding = 10.dp
