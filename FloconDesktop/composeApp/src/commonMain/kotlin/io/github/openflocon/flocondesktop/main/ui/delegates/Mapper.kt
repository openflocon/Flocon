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
    )

internal fun mapAppsToUi(devices: List<DeviceAppDomainModel>): List<DeviceAppUiModel> =
    devices.map {
        it.mapToUi()
    }


internal fun DeviceAppDomainModel.mapToUi() = DeviceAppUiModel(
    name = name,
    packageName = packageName,
    iconEncoded = iconEncoded,
)
