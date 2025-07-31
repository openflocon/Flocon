package io.github.openflocon.flocondesktop.features.images.data.datasources.local

import com.florent37.flocondesktop.DeviceId
import com.florent37.flocondesktop.features.images.data.datasources.local.model.DeviceImageEntity
import com.florent37.flocondesktop.features.images.domain.model.DeviceImageDomainModel

fun DeviceImageEntity.toDomainModel(): DeviceImageDomainModel = DeviceImageDomainModel(
    url = this.url,
    time = this.time,
)

fun DeviceImageDomainModel.toEntity(deviceId: DeviceId): DeviceImageEntity = DeviceImageEntity(
    deviceId = deviceId,
    url = this.url,
    time = this.time,
)
