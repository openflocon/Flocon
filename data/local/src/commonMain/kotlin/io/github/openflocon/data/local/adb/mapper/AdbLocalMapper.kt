package io.github.openflocon.data.local.adb.mapper

import io.github.openflocon.data.local.adb.model.DeviceWithSerialEntity
import io.github.openflocon.domain.adb.model.DeviceWithSerialDomainModel

fun toDomainModel(entity: DeviceWithSerialEntity): DeviceWithSerialDomainModel = DeviceWithSerialDomainModel(
    deviceId = entity.deviceId,
    serial = entity.serial
)

fun toEntity(model: DeviceWithSerialDomainModel): DeviceWithSerialEntity = DeviceWithSerialEntity(
    deviceId = model.deviceId,
    serial = model.serial
)
