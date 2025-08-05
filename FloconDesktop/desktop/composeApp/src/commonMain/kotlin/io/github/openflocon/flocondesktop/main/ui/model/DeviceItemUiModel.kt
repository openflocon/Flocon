package io.github.openflocon.flocondesktop.main.ui.model

data class DeviceItemUiModel(
    val appName: String,
    val deviceName: String,
    val appPackageName: String,
    val id: String,
)

fun previewDeviceItemUiModelPreview() = DeviceItemUiModel(
    appName = "appName",
    deviceName = "deviceName",
    appPackageName = "appPackageName",
    id = "id",
)
