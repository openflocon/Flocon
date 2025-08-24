package io.github.openflocon.domain.images.repository

import io.github.openflocon.domain.device.models.DeviceId
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.images.models.DeviceImageDomainModel
import kotlinx.coroutines.flow.Flow

interface ImagesRepository {
    fun observeImages(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<List<DeviceImageDomainModel>>
    suspend fun clearImages(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel)
}
