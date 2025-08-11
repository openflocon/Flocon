package io.github.openflocon.flocondesktop.features.images.domain.repository

import com.flocon.library.domain.models.DeviceId
import com.flocon.library.domain.models.DeviceImageDomainModel
import kotlinx.coroutines.flow.Flow

interface ImagesRepository {
    fun observeImages(deviceId: DeviceId): Flow<List<DeviceImageDomainModel>>
    suspend fun clearImages(deviceId: DeviceId)
}
