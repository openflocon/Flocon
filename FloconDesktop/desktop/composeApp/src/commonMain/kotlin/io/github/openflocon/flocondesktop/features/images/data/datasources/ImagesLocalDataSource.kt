package io.github.openflocon.flocondesktop.features.images.data.datasources

import io.github.openflocon.flocondesktop.DeviceId
import io.github.openflocon.flocondesktop.features.images.domain.model.DeviceImageDomainModel
import kotlinx.coroutines.flow.Flow

interface ImagesLocalDataSource {
    suspend fun addImage(
        deviceId: DeviceId,
        image: DeviceImageDomainModel,
    )

    fun observeImages(deviceId: DeviceId): Flow<List<DeviceImageDomainModel>>
    suspend fun clearImages(deviceId: DeviceId)
}
