package io.github.openflocon.data.core.images.datasource

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.images.models.DeviceImageDomainModel
import kotlinx.coroutines.flow.Flow

interface ImagesLocalDataSource {
    suspend fun addImage(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        image: DeviceImageDomainModel,
    )

    fun observeImages(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel
    ): Flow<List<DeviceImageDomainModel>>

    suspend fun clearImages(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel
    )
}
