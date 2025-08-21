package io.github.openflocon.data.local.network.datasource

import io.github.openflocon.data.core.network.datasource.NetworkQualityLocalDataSource
import io.github.openflocon.data.local.network.dao.NetworkBadQualityConfigDao
import io.github.openflocon.data.local.network.mapper.toDomain
import io.github.openflocon.data.local.network.mapper.toEntity
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.network.models.BadQualityConfigDomainModel
import io.github.openflocon.domain.network.models.BadQualityConfigId
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
            config.toEntity(
                json = json,
                deviceIdAndPackageName = deviceIdAndPackageName
            )
        )
    }

    override suspend fun getNetworkQuality(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        configId: BadQualityConfigId,
    ): BadQualityConfigDomainModel? {
        return networkBadQualityConfigDao.get(
            deviceId = deviceIdAndPackageName.deviceId,
            packageName = deviceIdAndPackageName.packageName,
            configId = configId
        )?.let {
            it.toDomain(
                json = json
            )
        }
    }

    override fun observe(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        configId: BadQualityConfigId,
    ): Flow<BadQualityConfigDomainModel?> {
        return networkBadQualityConfigDao.observe(
            deviceId = deviceIdAndPackageName.deviceId,
            packageName = deviceIdAndPackageName.packageName,
            configId = configId,
        ).map {
            it?.let {
                it.toDomain(
                    json = json
                )
            }
        }.distinctUntilChanged()
    }


    override fun observeAll(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): Flow<List<BadQualityConfigDomainModel>> {
        return networkBadQualityConfigDao.observeAll(
            deviceId = deviceIdAndPackageName.deviceId,
            packageName = deviceIdAndPackageName.packageName,
        ).map { list ->
            list.map {
                it.toDomain(
                    json = json
                )
            }
        }.distinctUntilChanged()
    }

    override suspend fun setEnabledConfig(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        configId: BadQualityConfigId?,
    ) {
        networkBadQualityConfigDao.setEnabledConfig(
            deviceId = deviceIdAndPackageName.deviceId,
            packageName = deviceIdAndPackageName.packageName,
            configId = configId,
        )
    }

    override suspend fun getTheOnlyEnabledNetworkQuality(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): BadQualityConfigDomainModel? {
        return networkBadQualityConfigDao.getTheOnlyEnabledNetworkQuality(
            deviceId = deviceIdAndPackageName.deviceId,
            packageName = deviceIdAndPackageName.packageName,
        )?.let {
            it.toDomain(
                json = json
            )
        }
    }

    override suspend fun delete(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        configId: BadQualityConfigId
    ) {
        networkBadQualityConfigDao.delete(
            deviceId = deviceIdAndPackageName.deviceId,
            packageName = deviceIdAndPackageName.packageName,
            configId = configId
        )
    }
}


