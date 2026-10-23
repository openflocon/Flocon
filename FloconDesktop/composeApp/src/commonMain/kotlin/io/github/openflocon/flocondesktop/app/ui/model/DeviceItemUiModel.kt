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
    // ip the device connects from, null when unknown or when connected through adb (loopback)
    val address: String? = null,
    // the tab of the device dropdown it belongs to
    val connectionType: ConnectionType = ConnectionType.Adb,
) {
    enum class ConnectionType {
        Adb,
        Network,
    }

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
