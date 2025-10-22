package io.github.openflocon.flocondesktop.menu.ui.model

import androidx.compose.runtime.Immutable

@Immutable
data class DeviceItemUiModel(
    val id: String,
    val deviceName: String,
    val isActive: Boolean,
    val platform: Platform,
    val canScreenshot: Boolean,
    val canScreenRecord: Boolean,
    val canRestart: Boolean,
) {
    enum class Platform {
        Android,
        Desktop,
        ios,
        Unknown,
    }
}

fun previewDeviceItemUiModel() = DeviceItemUiModel(
    id = "id",
    deviceName = "deviceName",
    isActive = true,
    platform = DeviceItemUiModel.Platform.Android,
    canScreenshot = true,
    canScreenRecord = true,
    canRestart = true,
)
