package io.github.openflocon.flocondesktop.main.ui.model

sealed interface DevicesStateUiModel {
    data object Loading : DevicesStateUiModel

    data object Empty : DevicesStateUiModel

    data class WithDevices(
        val devices: List<DeviceItemUiModel>,
        val selected: DeviceItemUiModel,
    ) : DevicesStateUiModel
}

fun previewDevicesStateUiModel(): DevicesStateUiModel = DevicesStateUiModel.WithDevices(
    devices =
    listOf(
        DeviceItemUiModel(
            appName = "appName1",
            deviceName = "deviceName1",
            appPackageName = "appPackageName1",
            id = "id1",
        ),
        DeviceItemUiModel(
            appName = "appName2",
            deviceName = "deviceName2",
            appPackageName = "appPackageName2",
            id = "id2",
        ),
        DeviceItemUiModel(
            appName = "appName",
            deviceName = "deviceName",
            appPackageName = "appPackageName",
            id = "id",
        ),
    ),
    selected =
    DeviceItemUiModel(
        appName = "appName",
        deviceName = "deviceName",
        appPackageName = "appPackageName",
        id = "id",
    ),
)
