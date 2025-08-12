package io.github.openflocon.data.local.images.datasource

import io.github.openflocon.data.core.images.datasource.ImagesLocalDataSource
import io.github.openflocon.data.local.images.dao.FloconImageDao
import io.github.openflocon.data.local.images.mapper.toDomainModel
import io.github.openflocon.data.local.images.mapper.toEntity
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.device.models.DeviceId
import io.github.openflocon.domain.images.models.DeviceImageDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal class ImagesLocalDataSourceRoom(
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

    override fun observeImages(deviceId: DeviceId): Flow<List<DeviceImageDomainModel>> = imageDao.observeImagesForDevice(deviceId)
        .map { entities -> entities.map { it.toDomainModel() } }
        .flowOn(dispatcherProvider.data)

    override suspend fun clearImages(deviceId: DeviceId) {
        withContext(dispatcherProvider.data) {
            imageDao.deleteAllImagesForDevice(deviceId)
        }
    }
}
