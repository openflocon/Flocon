package com.florent37.flocondesktop.features.images.data.datasources

import com.florent37.flocondesktop.DeviceId
import com.florent37.flocondesktop.features.images.domain.model.DeviceImageDomainModel
import kotlinx.coroutines.flow.Flow

interface ImagesLocalDataSource {
    suspend fun addImage(
        deviceId: DeviceId,
        image: DeviceImageDomainModel,
    )

    fun observeImages(deviceId: DeviceId): Flow<List<DeviceImageDomainModel>>
    suspend fun clearImages(deviceId: DeviceId)
}
