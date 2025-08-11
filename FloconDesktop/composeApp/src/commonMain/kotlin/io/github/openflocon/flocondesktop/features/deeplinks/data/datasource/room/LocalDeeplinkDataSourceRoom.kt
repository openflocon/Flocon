package io.github.openflocon.flocondesktop.features.deeplinks.data.datasource.room

import io.github.openflocon.flocondesktop.features.deeplinks.data.datasource.LocalDeeplinkDataSource
import io.github.openflocon.flocondesktop.features.deeplinks.data.datasource.room.mapper.toDomainModels
import io.github.openflocon.flocondesktop.features.deeplinks.data.datasource.room.mapper.toEntities
import io.github.openflocon.domain.deeplink.models.DeeplinkDomainModel
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class LocalDeeplinkDataSourceRoom(
    private val deeplinkDao: FloconDeeplinkDao,
) : LocalDeeplinkDataSource {
    override suspend fun update(deviceIdAndPackageNameDomainModel: DeviceIdAndPackageNameDomainModel, deeplinks: List<DeeplinkDomainModel>) {
        deeplinkDao.updateAll(
            deviceId = deviceIdAndPackageNameDomainModel.deviceId,
            packageName = deviceIdAndPackageNameDomainModel.packageName,
            deeplinks = toEntities(
                deeplinks = deeplinks,
                deviceIdAndPackageName = deviceIdAndPackageNameDomainModel,
            ),
        )
    }

    override fun observe(deviceIdAndPackageNameDomainModel: DeviceIdAndPackageNameDomainModel): Flow<List<DeeplinkDomainModel>> = deeplinkDao.observeAll(
        deviceId = deviceIdAndPackageNameDomainModel.deviceId,
        packageName = deviceIdAndPackageNameDomainModel.packageName,
    )
        .map { toDomainModels(it) }
        .distinctUntilChanged()
}
