package io.github.openflocon.flocondesktop.menu.ui

import androidx.compose.runtime.Immutable
import io.github.openflocon.flocondesktop.menu.ui.model.AppsStateUiModel
import io.github.openflocon.flocondesktop.menu.ui.model.DevicesStateUiModel
import io.github.openflocon.flocondesktop.menu.ui.model.RecordVideoStateUiModel
import io.github.openflocon.flocondesktop.menu.ui.model.SubScreen
import io.github.openflocon.flocondesktop.menu.ui.model.leftpanel.LeftPanelState
import io.github.openflocon.flocondesktop.menu.ui.model.leftpanel.previewLeftPannelState

@Immutable
data class MenuUiState(
    val leftPanelState: LeftPanelState,
    val recordVideoState: RecordVideoStateUiModel,
    val appsStateUiModel: AppsStateUiModel,
    val devicesStateUiModel: DevicesStateUiModel
)

fun previewMenuUiState() = MenuUiState(
    leftPanelState = previewLeftPannelState(SubScreen.Network),
    recordVideoState = RecordVideoStateUiModel.Idle,
    appsStateUiModel = AppsStateUiModel.Empty,
    devicesStateUiModel = DevicesStateUiModel.Empty
)
