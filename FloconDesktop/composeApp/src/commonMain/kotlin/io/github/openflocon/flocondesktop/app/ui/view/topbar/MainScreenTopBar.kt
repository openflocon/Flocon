package io.github.openflocon.flocondesktop.app.ui.view.topbar

import androidx.compose.foundation.Image
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
import flocondesktop.composeapp.generated.resources.app_icon_small
import flocondesktop.composeapp.generated.resources.app_logo_content_description
import flocondesktop.composeapp.generated.resources.app_name
import io.github.openflocon.flocondesktop.BuildConfig
import io.github.openflocon.flocondesktop.app.ui.model.AppsStateUiModel
import io.github.openflocon.flocondesktop.app.ui.model.DeviceAppUiModel
import io.github.openflocon.flocondesktop.app.ui.model.DeviceItemUiModel
import io.github.openflocon.flocondesktop.app.ui.model.DevicesStateUiModel
import io.github.openflocon.flocondesktop.app.ui.model.RecordVideoStateUiModel
import io.github.openflocon.flocondesktop.app.ui.view.topbar.actions.TopBarActions
import io.github.openflocon.flocondesktop.app.version.VersionCheckerViewModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.floconIsland
import org.jetbrains.compose.resources.painterResource
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
    onRestartClicked: () -> Unit,
    onOpenFolderClicked: () -> Unit,
    onClickDetail: (DeviceItemUiModel) -> Unit,
    modifier: Modifier = Modifier,
    updateChip: VersionCheckerViewModel.UpdateChipUiModel? = null,
    onUpdateChipClicked: (VersionCheckerViewModel.UpdateChipUiModel) -> Unit = {},
) {
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
            updateChip = updateChip,
            onUpdateChipClicked = onUpdateChipClicked,
            modifier = Modifier.padding(end = FloconTheme.spacing.medium),
        )
        TopBarDeviceAndApp(
            devicesState = devicesState,
            appsState = appsState,
            onDeviceSelected = onDeviceSelected,
            onAppSelected = onAppSelected,
            deleteDevice = deleteDevice,
            deleteApp = deleteApp,
            onClickDetail = onClickDetail,
        )
        Spacer(modifier = Modifier.weight(1f))
        TopBarActions(
            onTakeScreenshotClicked = onTakeScreenshotClicked,
            recordState = recordState,
            onRecordClicked = onRecordClicked,
            onRestartClicked = onRestartClicked,
            onOpenFolderClicked = onOpenFolderClicked,
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
    val colors = FloconTheme.colors

    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(colors.accentSoft)
            .clickable(onClick = onClick)
            .pointerHoverIcon(PointerIcon.Hand)
            .padding(horizontal = FloconTheme.spacing.small, vertical = FloconTheme.spacing.tiny),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.compact),
    ) {
        Icon(
            imageVector = Icons.Outlined.ArrowCircleUp,
            contentDescription = null,
            tint = colors.accent,
            modifier = Modifier.size(12.dp),
        )
        Text(
            text = uiModel.text,
            color = colors.accent,
            style = FloconTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
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
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(10.dp)),
            painter = painterResource(Res.drawable.app_icon_small),
            contentDescription = stringResource(Res.string.app_logo_content_description),
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

        if (updateChip != null) {
            TopBarUpdateChip(
                uiModel = updateChip,
                onClick = { onUpdateChipClicked(updateChip) },
            )
        }
    }
}

private val TopBarVerticalPadding = 10.dp
