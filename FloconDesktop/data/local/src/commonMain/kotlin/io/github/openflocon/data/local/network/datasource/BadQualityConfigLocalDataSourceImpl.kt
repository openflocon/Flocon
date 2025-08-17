package io.github.openflocon.data.local.network.datasource

import io.github.openflocon.data.core.network.datasource.NetworkQualityLocalDataSource
import io.github.openflocon.data.local.network.dao.NetworkBadQualityConfigDao
import io.github.openflocon.data.local.network.mapper.toDomain
import io.github.openflocon.data.local.network.mapper.toEntity
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.network.models.BadQualityConfigDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class BadQualityConfigLocalDataSourceImpl(
    private val networkBadQualityConfigDao: NetworkBadQualityConfigDao,
    private val json: Json,
) : NetworkQualityLocalDataSource {

    override suspend fun save(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        config: BadQualityConfigDomainModel
    ) {
        networkBadQualityConfigDao.save(
            toEntity(
                json = json,
                config = config,
                deviceIdAndPackageName = deviceIdAndPackageName
            )
        )
    }

    override suspend fun getNetworkQuality(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): BadQualityConfigDomainModel? {
        return networkBadQualityConfigDao.get(
            deviceId = deviceIdAndPackageName.deviceId,
            packageName = deviceIdAndPackageName.packageName
        )?.let {
            toDomain(
                json = json,
                entity = it
            )
        }
    }

    override fun observe(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel
    ): Flow<BadQualityConfigDomainModel?> {
        return networkBadQualityConfigDao.observe(
            deviceId = deviceIdAndPackageName.deviceId,
            packageName = deviceIdAndPackageName.packageName
        ).map {
            it?.let {
                toDomain(
                    json = json,
                    entity = it
                )
            }
        }.distinctUntilChanged()
    }

    override suspend fun updateIsEnabled(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        isEnabled: Boolean,
    ) {
        networkBadQualityConfigDao.updateIsEnabled(
            deviceId = deviceIdAndPackageName.deviceId,
            packageName = deviceIdAndPackageName.packageName,
            isEnabled = isEnabled
        )
    }
}


