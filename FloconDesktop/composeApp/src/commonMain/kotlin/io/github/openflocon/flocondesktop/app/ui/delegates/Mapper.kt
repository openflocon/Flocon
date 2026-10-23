package io.github.openflocon.flocondesktop.app.ui.delegates

import io.github.openflocon.domain.device.models.DeviceAppDomainModel
import io.github.openflocon.domain.device.models.DeviceCapabilitiesDomainModel
import io.github.openflocon.domain.device.models.DeviceDomainModel
import io.github.openflocon.domain.device.models.DeviceId
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.flocondesktop.app.ui.model.DeviceAppUiModel
import io.github.openflocon.flocondesktop.app.ui.model.DeviceItemUiModel

internal fun mapListToUi(
    devices: List<DeviceDomainModel>,
    activeDevices: Set<DeviceIdAndPackageNameDomainModel>,
    activeAddresses: Map<DeviceId, String>,
): List<DeviceItemUiModel> = devices.map {
    it.mapToUi(
        activeDevices = activeDevices,
        activeAddresses = activeAddresses,
        currentDeviceCapabilities = null
    )
}

internal fun DeviceDomainModel.mapToUi(
    activeDevices: Set<DeviceIdAndPackageNameDomainModel>,
    activeAddresses: Map<DeviceId, String>,
    currentDeviceCapabilities: DeviceCapabilitiesDomainModel?,
): DeviceItemUiModel = DeviceItemUiModel(
    deviceName = deviceName,
    id = deviceId,
    isActive = activeDevices.any { it.deviceId == deviceId },
    address = activeAddresses[deviceId]?.takeUnless(::isLoopbackAddress),
    platform = mapToPlatformUi(platform),
    canScreenshot = currentDeviceCapabilities?.screenshot == true,
    canScreenRecord = currentDeviceCapabilities?.recordScreen == true,
    canRestart = currentDeviceCapabilities?.restart == true,
)

// a device connected through `adb reverse` reaches the desktop from the loopback interface: nothing useful to show
internal fun isLoopbackAddress(address: String): Boolean = address == "localhost" ||
    address.startsWith("127.") ||
    address == "::1" ||
    address == "0:0:0:0:0:0:0:1"

private fun mapToPlatformUi(platform: String): DeviceItemUiModel.Platform = when (platform) {
    "android" -> DeviceItemUiModel.Platform.Android
    "desktop" -> DeviceItemUiModel.Platform.Desktop
    "ios" -> DeviceItemUiModel.Platform.ios
    else -> DeviceItemUiModel.Platform.Unknown
}

internal fun mapAppsToUi(devices: List<DeviceAppDomainModel>): List<DeviceAppUiModel> = devices.map {
    it.mapToUi()
}

internal fun DeviceAppDomainModel.mapToUi() = DeviceAppUiModel(
    name = name,
    packageName = packageName,
    iconEncoded = iconEncoded,
)
