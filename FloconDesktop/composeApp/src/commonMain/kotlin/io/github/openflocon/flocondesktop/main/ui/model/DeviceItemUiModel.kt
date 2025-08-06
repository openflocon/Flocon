package io.github.openflocon.flocondesktop.main.ui.model

import androidx.compose.runtime.Immutable

@Immutable
data class DeviceItemUiModel(
    val id: String,
    val deviceName: String,
    val apps: List<DeviceAppUiModel>
)

fun previewDeviceItemUiModel() = DeviceItemUiModel(
    deviceName = "deviceName",
    id = "id",
    apps = listOf(previewDeviceAppUiModel())
)
