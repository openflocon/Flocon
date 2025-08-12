package io.github.openflocon.data.local.deeplink.datasource

import io.github.openflocon.data.core.deeplink.datasource.DeeplinkLocalDataSource
import io.github.openflocon.data.local.deeplink.dao.FloconDeeplinkDao
import io.github.openflocon.data.local.deeplink.mapper.toDomainModels
import io.github.openflocon.data.local.deeplink.mapper.toEntities
import io.github.openflocon.domain.deeplink.models.DeeplinkDomainModel
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

internal class LocalDeeplinkDataSourceRoom(
    private val deeplinkDao: FloconDeeplinkDao,
) : DeeplinkLocalDataSource {
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

    override fun observe(deviceIdAndPackageNameDomainModel: DeviceIdAndPackageNameDomainModel): Flow<List<DeeplinkDomainModel>> =
        deeplinkDao.observeAll(
            deviceId = deviceIdAndPackageNameDomainModel.deviceId,
            packageName = deviceIdAndPackageNameDomainModel.packageName,
        )
            .map { toDomainModels(it) }
            .distinctUntilChanged()
}
