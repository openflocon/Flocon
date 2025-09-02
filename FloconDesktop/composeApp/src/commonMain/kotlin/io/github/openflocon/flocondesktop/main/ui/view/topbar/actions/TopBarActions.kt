package io.github.openflocon.flocondesktop.main.ui.view.topbar.actions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.StopCircle
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.main.ui.model.DevicesStateUiModel
import io.github.openflocon.flocondesktop.main.ui.model.RecordVideoStateUiModel

@Composable
internal fun TopBarActions(
    onTakeScreenshotClicked: () -> Unit,
    recordState: RecordVideoStateUiModel,
    devicesState: DevicesStateUiModel,
    onRecordClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        TopBarButton(
            active = recordState == RecordVideoStateUiModel.Recording,
            imageVector = when (recordState) {
                RecordVideoStateUiModel.Idle -> Icons.Outlined.Videocam
                RecordVideoStateUiModel.Recording -> Icons.Outlined.StopCircle
            },
            contentDescription = "Record",
            onClicked = onRecordClicked,
            isEnabled = devicesState.deviceSelected?.isActive == true,
        )
        TopBarButton(
            active = false,
            imageVector = Icons.Outlined.CameraAlt,
            contentDescription = "screenshot",
            onClicked = onTakeScreenshotClicked,
            isEnabled = devicesState.deviceSelected?.isActive == true,
        )
    }
}

