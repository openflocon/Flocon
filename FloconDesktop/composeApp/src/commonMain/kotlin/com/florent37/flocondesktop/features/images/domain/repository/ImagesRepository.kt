package com.florent37.flocondesktop.features.images.domain.repository

import com.florent37.flocondesktop.DeviceId
import com.florent37.flocondesktop.features.images.domain.model.DeviceImageDomainModel
import kotlinx.coroutines.flow.Flow

interface ImagesRepository {
    fun observeImages(deviceId: DeviceId): Flow<List<DeviceImageDomainModel>>
    suspend fun clearImages(deviceId: DeviceId)
}
