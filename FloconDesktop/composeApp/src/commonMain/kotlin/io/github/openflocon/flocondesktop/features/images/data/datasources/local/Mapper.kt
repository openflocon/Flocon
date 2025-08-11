package io.github.openflocon.flocondesktop.features.images.data.datasources.local

import com.flocon.library.domain.models.DeviceId
import io.github.openflocon.flocondesktop.features.images.data.datasources.local.model.DeviceImageEntity
import com.flocon.library.domain.models.DeviceImageDomainModel

fun DeviceImageEntity.toDomainModel(): DeviceImageDomainModel = DeviceImageDomainModel(
    url = this.url,
    time = this.time,
)

fun DeviceImageDomainModel.toEntity(deviceId: DeviceId): DeviceImageEntity = DeviceImageEntity(
    deviceId = deviceId,
    url = this.url,
    time = this.time,
)
