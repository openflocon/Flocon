package io.github.openflocon.flocondesktop.app.ui.delegates

import io.github.openflocon.domain.device.models.DeviceAppDomainModel
import io.github.openflocon.domain.device.models.DeviceCapabilitiesDomainModel
import io.github.openflocon.domain.device.models.DeviceConnectionTypeDomainModel
import io.github.openflocon.domain.device.models.DeviceDomainModel
import io.github.openflocon.domain.device.models.DeviceId
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.device.models.isLoopbackAddress
import io.github.openflocon.flocondesktop.app.ui.model.DeviceAppUiModel
import io.github.openflocon.flocondesktop.app.ui.model.DeviceItemUiModel

internal fun mapListToUi(
    devices: List<DeviceDomainModel>,
    activeDevices: Set<DeviceIdAndPackageNameDomainModel>,
    activeAddresses: Map<DeviceId, String>,
    connectionTypes: Map<DeviceId, DeviceConnectionTypeDomainModel>,
): List<DeviceItemUiModel> = devices.map {
    it.mapToUi(
        activeDevices = activeDevices,
        activeAddresses = activeAddresses,
        connectionTypes = connectionTypes,
        currentDeviceCapabilities = null
    )
}

internal fun DeviceDomainModel.mapToUi(
    activeDevices: Set<DeviceIdAndPackageNameDomainModel>,
    activeAddresses: Map<DeviceId, String>,
    connectionTypes: Map<DeviceId, DeviceConnectionTypeDomainModel>,
    currentDeviceCapabilities: DeviceCapabilitiesDomainModel?,
): DeviceItemUiModel = DeviceItemUiModel(
    deviceName = deviceName,
    id = deviceId,
    isActive = activeDevices.any { it.deviceId == deviceId },
    // a device connected through `adb reverse` reaches the desktop from the loopback interface: nothing useful to show
    address = activeAddresses[deviceId]?.takeUnless(::isLoopbackAddress),
    // never seen connecting: adb, the historical way
    connectionType = when (connectionTypes[deviceId]) {
        DeviceConnectionTypeDomainModel.Network -> DeviceItemUiModel.ConnectionType.Network
        DeviceConnectionTypeDomainModel.Adb, null -> DeviceItemUiModel.ConnectionType.Adb
    },
    platform = mapToPlatformUi(platform),
    canScreenshot = currentDeviceCapabilities?.screenshot == true,
    canScreenRecord = currentDeviceCapabilities?.recordScreen == true,
    canRestart = currentDeviceCapabilities?.restart == true,
)

internal fun List<DeviceItemUiModel>.inTab(connectionType: DeviceItemUiModel.ConnectionType): List<DeviceItemUiModel> = filter { it.connectionType == connectionType }

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
