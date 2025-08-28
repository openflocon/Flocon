package io.github.openflocon.data.local.device.datasource.mapper

import io.github.openflocon.data.local.device.datasource.model.DeviceAppEntity
import io.github.openflocon.data.local.device.datasource.model.DeviceEntity
import io.github.openflocon.domain.device.models.DeviceAppDomainModel
import io.github.openflocon.domain.device.models.DeviceDomainModel

internal fun DeviceAppEntity.toDomainModel(): DeviceAppDomainModel {
    return DeviceAppDomainModel(
        name = this.name,
        packageName = this.packageName,
        iconEncoded = this.iconEncoded,
        lastAppInstance = this.lastAppInstance,
    )
}

internal fun DeviceDomainModel.toEntity(): DeviceEntity {
    return DeviceEntity(
        deviceId = this.deviceId,
        deviceName = this.deviceName
    )
}

internal fun DeviceEntity.toDomainModel(): DeviceDomainModel {
    return DeviceDomainModel(
        deviceId = this.deviceId,
        deviceName = this.deviceName
    )
}

internal fun DeviceAppDomainModel.toEntity(deviceId: String): DeviceAppEntity {
    return DeviceAppEntity(
        deviceId = deviceId,
        name = this.name,
        packageName = this.packageName,
        iconEncoded = this.iconEncoded,
        lastAppInstance = this.lastAppInstance,
    )
}
