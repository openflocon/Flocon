package io.github.openflocon.flocondesktop.app

import androidx.compose.runtime.Immutable
import io.github.openflocon.flocondesktop.menu.ui.model.AppsStateUiModel
import io.github.openflocon.flocondesktop.menu.ui.model.DevicesStateUiModel
import io.github.openflocon.flocondesktop.menu.ui.model.RecordVideoStateUiModel
import io.github.openflocon.flocondesktop.menu.ui.model.previewAppsStateUiModel
import io.github.openflocon.flocondesktop.menu.ui.model.previewDevicesStateUiModel

@Immutable
data class AppUiState(
    val contentState: ContentUiState,
    val deviceState: DevicesStateUiModel,
    val appState: AppsStateUiModel,
    val recordState: RecordVideoStateUiModel
)

fun previewAppUiState() = AppUiState(
    contentState = previewContentUiState(),
    deviceState = previewDevicesStateUiModel(),
    appState = previewAppsStateUiModel(),
    recordState = RecordVideoStateUiModel.Recording
)
