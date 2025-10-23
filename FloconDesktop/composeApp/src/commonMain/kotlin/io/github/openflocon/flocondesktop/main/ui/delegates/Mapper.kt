package io.github.openflocon.flocondesktop.main.ui.delegates

import io.github.openflocon.domain.device.models.DeviceAppDomainModel
import io.github.openflocon.domain.device.models.DeviceDomainModel
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.flocondesktop.main.ui.model.DeviceAppUiModel
import io.github.openflocon.flocondesktop.main.ui.model.DeviceItemUiModel

internal fun mapListToUi(
    devices: List<DeviceDomainModel>,
    activeDevices: Set<DeviceIdAndPackageNameDomainModel>
): List<DeviceItemUiModel> = devices.map {
    it.mapToUi(activeDevices = activeDevices)
}

internal fun DeviceDomainModel.mapToUi(activeDevices: Set<DeviceIdAndPackageNameDomainModel>): DeviceItemUiModel =
    DeviceItemUiModel(
        deviceName = deviceName,
        id = deviceId,
        isActive = activeDevices.any { it.deviceId == deviceId },
        platform = mapToPlatformUi(platform)
    )


private fun mapToPlatformUi(platform: String): DeviceItemUiModel.Platform {
    return when (platform) {
        "android" -> DeviceItemUiModel.Platform.Android
        "desktop" -> DeviceItemUiModel.Platform.Desktop
        "ios" -> DeviceItemUiModel.Platform.ios
        else -> DeviceItemUiModel.Platform.Unknown
    }
}


internal fun mapAppsToUi(devices: List<DeviceAppDomainModel>): List<DeviceAppUiModel> =
    devices.map {
        it.mapToUi()
    }


internal fun DeviceAppDomainModel.mapToUi() = DeviceAppUiModel(
    name = name,
    packageName = packageName,
    iconEncoded = iconEncoded,
)
