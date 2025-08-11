package io.github.openflocon.flocondesktop.features.images.data.datasources

import io.github.openflocon.domain.device.models.DeviceId
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.flocondesktop.features.images.data.datasources.local.FloconImageDao
import io.github.openflocon.flocondesktop.features.images.data.datasources.local.toDomainModel
import io.github.openflocon.flocondesktop.features.images.data.datasources.local.toEntity
import io.github.openflocon.domain.images.models.DeviceImageDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ImagesLocalDataSourceRoom(
    private val imageDao: FloconImageDao,
    private val dispatcherProvider: DispatcherProvider,
) : ImagesLocalDataSource {

    override suspend fun addImage(
        deviceId: DeviceId,
        image: DeviceImageDomainModel,
    ) {
        withContext(dispatcherProvider.data) {
            imageDao.insertImage(
                image.toEntity(deviceId),
            )
        }
    }

    override fun observeImages(deviceId: DeviceId): Flow<List<DeviceImageDomainModel>> = imageDao.observeImagesForDevice(deviceId).map { entities ->
        entities.map { it.toDomainModel() }
    }.flowOn(dispatcherProvider.data)

    override suspend fun clearImages(deviceId: DeviceId) {
        withContext(dispatcherProvider.data) {
            imageDao.deleteAllImagesForDevice(deviceId)
        }
    }
}
