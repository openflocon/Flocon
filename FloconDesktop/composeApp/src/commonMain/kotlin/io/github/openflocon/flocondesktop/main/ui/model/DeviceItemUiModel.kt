package io.github.openflocon.flocondesktop.main.ui.model

import androidx.compose.runtime.Immutable

@Immutable
data class DeviceItemUiModel(
    val id: String,
    val deviceName: String,
    val isActive: Boolean,
)

fun previewDeviceItemUiModel() = DeviceItemUiModel(
    id = "id",
    deviceName = "deviceName",
    isActive = true,
)
