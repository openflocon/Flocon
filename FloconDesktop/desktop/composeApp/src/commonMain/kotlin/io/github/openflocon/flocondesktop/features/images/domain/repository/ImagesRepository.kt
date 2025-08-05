package io.github.openflocon.flocondesktop.features.images.domain.repository

import io.github.openflocon.flocondesktop.DeviceId
import io.github.openflocon.flocondesktop.features.images.domain.model.DeviceImageDomainModel
import kotlinx.coroutines.flow.Flow

interface ImagesRepository {
    fun observeImages(deviceId: DeviceId): Flow<List<DeviceImageDomainModel>>
    suspend fun clearImages(deviceId: DeviceId)
}
