package io.github.openflocon.data.local.device.datasource.mapper

import io.github.openflocon.data.local.device.datasource.model.DeviceAppEntity
import io.github.openflocon.data.local.device.datasource.model.DeviceEntity
import io.github.openflocon.domain.device.models.DeviceAppDomainModel
import io.github.openflocon.domain.device.models.DeviceDomainModel

internal fun DeviceAppEntity.toDomainModel(): DeviceAppDomainModel = DeviceAppDomainModel(
    name = this.name,
    packageName = this.packageName,
    iconEncoded = this.iconEncoded,
    lastAppInstance = this.lastAppInstance,
    floconVersionOnDevice = this.floconVersionOnDevice,
)

internal fun DeviceDomainModel.toEntity(): DeviceEntity = DeviceEntity(
    deviceId = this.deviceId,
    deviceName = this.deviceName,
    platform = this.platform,
)

internal fun DeviceEntity.toDomainModel(): DeviceDomainModel = DeviceDomainModel(
    deviceId = this.deviceId,
    deviceName = this.deviceName,
    platform = this.platform,
)

internal fun DeviceAppDomainModel.toEntity(deviceId: String): DeviceAppEntity = DeviceAppEntity(
    deviceId = deviceId,
    name = this.name,
    packageName = this.packageName,
    iconEncoded = this.iconEncoded,
    lastAppInstance = this.lastAppInstance,
    floconVersionOnDevice = this.floconVersionOnDevice,
)
