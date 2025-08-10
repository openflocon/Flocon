package io.github.openflocon.flocondesktop.main.ui.model

import androidx.compose.runtime.Immutable

@Immutable
sealed interface DevicesStateUiModel {

    @Immutable
    data object Loading : DevicesStateUiModel

    @Immutable
    data object Empty : DevicesStateUiModel

    @Immutable
    data class WithDevices(
        val devices: List<DeviceItemUiModel>,
        val deviceSelected: DeviceItemUiModel,
        val appSelected: DeviceAppUiModel?,
    ) : DevicesStateUiModel
}

fun previewDevicesStateUiModel(): DevicesStateUiModel = DevicesStateUiModel.WithDevices(
    devices = listOf(
        DeviceItemUiModel(
            deviceName = "deviceName1",
            id = "id1",
            apps = listOf(
                DeviceAppUiModel(
                    name = "appName1",
                    packageName = "packageName1",
                ),
            ),
        ),
        DeviceItemUiModel(
            deviceName = "deviceName2",
            id = "id2",
            apps = listOf(
                DeviceAppUiModel(
                    name = "appName2",
                    packageName = "packageName2",
                ),
            ),
        ),
        DeviceItemUiModel(
            deviceName = "deviceName",
            id = "id",
            apps = listOf(
                DeviceAppUiModel(
                    name = "appName",
                    packageName = "packageName",
                ),
            ),
        ),
    ),
    appSelected = null,
    deviceSelected = DeviceItemUiModel(
        deviceName = "deviceName",
        id = "id",
        apps = listOf(
            DeviceAppUiModel(
                name = "appName",
                packageName = "packageName",
            ),
        ),
    ),
)
