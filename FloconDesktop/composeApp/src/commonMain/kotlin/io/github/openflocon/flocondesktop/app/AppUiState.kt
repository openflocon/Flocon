package io.github.openflocon.flocondesktop.app

import androidx.compose.runtime.Immutable
import io.github.openflocon.flocondesktop.app.ui.model.AppsStateUiModel
import io.github.openflocon.flocondesktop.app.ui.model.DevicesStateUiModel
import io.github.openflocon.flocondesktop.app.ui.model.RecordVideoStateUiModel
import io.github.openflocon.flocondesktop.app.ui.model.leftpanel.MenuState
import io.github.openflocon.flocondesktop.app.ui.model.leftpanel.previewMenuState
import io.github.openflocon.flocondesktop.app.ui.model.previewAppsStateUiModel
import io.github.openflocon.flocondesktop.app.ui.model.previewDevicesStateUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class AppUiState(
    val contentState: ContentUiState,
    val menuState: MenuState,
    val deviceState: DevicesStateUiModel,
    val appState: AppsStateUiModel,
    val recordState: RecordVideoStateUiModel,
    // ws://<ip>:<port> urls of this machine, to fill FloconConnectionMode.Url in an app
    val localUrls: ImmutableList<String> = persistentListOf(),
) {
    // the selected device is connected right now; true while loading so the pages don't flash
    val hasConnectedDevice: Boolean
        get() = when (deviceState) {
            DevicesStateUiModel.Loading -> true
            DevicesStateUiModel.Empty -> false
            is DevicesStateUiModel.WithDevices -> deviceState.deviceSelected.isActive
        }
}

fun previewAppUiState() = AppUiState(
    contentState = previewContentUiState(),
    menuState = previewMenuState(),
    deviceState = previewDevicesStateUiModel(),
    appState = previewAppsStateUiModel(),
    recordState = RecordVideoStateUiModel.Recording,
    localUrls = persistentListOf("ws://192.168.1.20:9023"),
)
