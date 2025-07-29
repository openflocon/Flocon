package com.florent37.flocondesktop.features.images.data.datasources

import com.florent37.flocondesktop.DeviceId
import com.florent37.flocondesktop.common.coroutines.dispatcherprovider.DispatcherProvider
import com.florent37.flocondesktop.features.images.data.datasources.local.FloconImageDao
import com.florent37.flocondesktop.features.images.data.datasources.local.toDomainModel
import com.florent37.flocondesktop.features.images.data.datasources.local.toEntity
import com.florent37.flocondesktop.features.images.domain.model.DeviceImageDomainModel
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
