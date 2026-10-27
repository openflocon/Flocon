package io.github.openflocon.flocondesktop.app.ui.model

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

fun previewDeviceItemUiModel(
    platform: DeviceItemUiModel.Platform = DeviceItemUiModel.Platform.Android,
    isActive: Boolean = true,
    deviceName: String = "Pixel 8 Pro",
) = DeviceItemUiModel(
    id = "$platform-$deviceName-$isActive",
    deviceName = deviceName,
    isActive = isActive,
    platform = platform,
    canScreenshot = true,
    canScreenRecord = true,
    canRestart = true,
)
