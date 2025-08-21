package io.github.openflocon.flocondesktop.main.ui.delegates

import io.github.openflocon.domain.device.models.DeviceAppDomainModel
import io.github.openflocon.domain.device.models.DeviceDomainModel
import io.github.openflocon.flocondesktop.main.ui.model.DeviceAppUiModel
import io.github.openflocon.flocondesktop.main.ui.model.DeviceItemUiModel

internal fun mapToUi(devices: List<DeviceDomainModel>): List<DeviceItemUiModel> = devices.map {
    it.mapToUi()
}

internal fun DeviceDomainModel.mapToUi(): DeviceItemUiModel = DeviceItemUiModel(
    deviceName = deviceName,
    id = deviceId,
)

internal fun mapAppsToUi(devices: List<DeviceAppDomainModel>): List<DeviceAppUiModel> = devices.map {
    it.mapToUi()
}


internal fun DeviceAppDomainModel.mapToUi() = DeviceAppUiModel(
    name = name,
    packageName = packageName,
    iconEncoded = iconEncoded,
)
