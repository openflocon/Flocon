package io.github.openflocon.data.local.images.datasource

import io.github.openflocon.data.core.images.datasource.ImagesLocalDataSource
import io.github.openflocon.data.local.images.dao.FloconImageDao
import io.github.openflocon.data.local.images.mapper.toDomainModel
import io.github.openflocon.data.local.images.mapper.toEntity
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.images.models.DeviceImageDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

internal class ImagesLocalDataSourceRoom(
    private val imageDao: FloconImageDao,
    private val dispatcherProvider: DispatcherProvider,
    private val json: Json,
) : ImagesLocalDataSource {

    override suspend fun addImage(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        image: DeviceImageDomainModel,
    ) {
        withContext(dispatcherProvider.data) {
            imageDao.insertImage(
                image.toEntity(
                    deviceIdAndPackageName = deviceIdAndPackageName,
                    json = json,
                ),
            )
        }
    }

    override fun observeImages(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): Flow<List<DeviceImageDomainModel>> = imageDao.observeImagesForDevice(
        deviceId = deviceIdAndPackageName.deviceId,
        packageName = deviceIdAndPackageName.packageName,
    )
        .map { entities ->
            entities.map {
                it.toDomainModel(
                    json = json,
                )
            }
        }
        .flowOn(dispatcherProvider.data)

    override suspend fun clearImages(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ) {
        withContext(dispatcherProvider.data) {
            imageDao.deleteAllImagesForDevice(
                deviceId = deviceIdAndPackageName.deviceId,
                packageName = deviceIdAndPackageName.packageName,
            )
        }
    }
}
