package io.github.openflocon.data.local.images.mapper

import io.github.openflocon.data.local.images.models.DeviceImageEntity
import io.github.openflocon.domain.device.models.DeviceId
import io.github.openflocon.domain.images.models.DeviceImageDomainModel

fun DeviceImageEntity.toDomainModel(): DeviceImageDomainModel = DeviceImageDomainModel(
    url = this.url,
    time = this.time,
)

fun DeviceImageDomainModel.toEntity(deviceId: DeviceId): DeviceImageEntity = DeviceImageEntity(
    deviceId = deviceId,
    url = this.url,
    time = this.time,
)
