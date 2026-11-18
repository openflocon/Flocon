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
import kotlin.time.Instant

class BadQualityConfigLocalDataSourceImpl(
    private val networkBadQualityConfigDao: NetworkBadQualityConfigDao,
    private val json: Json,
) : NetworkQualityLocalDataSource {

    override suspend fun save(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        config: BadQualityConfigDomainModel
    ) {
        networkBadQualityConfigDao.save(
            listOf(
                config.toEntity(
                    json = json,
                    deviceIdAndPackageName = deviceIdAndPackageName
                )
            )
        )
    }

    override suspend fun getNetworkQuality(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        configId: BadQualityConfigId,
    ): BadQualityConfigDomainModel? = networkBadQualityConfigDao.get(
        deviceId = deviceIdAndPackageName.deviceId,
        packageName = deviceIdAndPackageName.packageName,
        configId = configId
    )?.let {
        it.toDomain(
            json = json
        )
    }

    override fun observe(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        configId: BadQualityConfigId,
    ): Flow<BadQualityConfigDomainModel?> = networkBadQualityConfigDao.observe(
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

    override fun observeAll(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): Flow<List<BadQualityConfigDomainModel>> = networkBadQualityConfigDao.observeAll(
        deviceId = deviceIdAndPackageName.deviceId,
        packageName = deviceIdAndPackageName.packageName,
    ).map { list ->
        list.map {
            it.toDomain(
                json = json
            )
        }
    }.distinctUntilChanged()

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
    ): BadQualityConfigDomainModel? = networkBadQualityConfigDao.getTheOnlyEnabledNetworkQuality(
        deviceId = deviceIdAndPackageName.deviceId,
        packageName = deviceIdAndPackageName.packageName,
    )?.let {
        it.toDomain(
            json = json
        )
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

    override suspend fun prepopulate(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel) {
        networkBadQualityConfigDao.saveIfNotExists(
            defaultConfig(deviceIdAndPackageName = deviceIdAndPackageName).map {
                it.toEntity(
                    json = json,
                    deviceIdAndPackageName = deviceIdAndPackageName,
                )
            }
        )
    }

    private fun defaultConfig(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): List<BadQualityConfigDomainModel> {
        // to be sure we don't have doubles
        val idPrefix = deviceIdAndPackageName.deviceId + deviceIdAndPackageName.packageName
        val now = System.currentTimeMillis()
        return listOf(
            BadQualityConfigDomainModel(
                id = idPrefix + "low_latency",
                isEnabled = false,
                name = "Low latency",
                createdAt = Instant.fromEpochMilliseconds(now + 1),
                latency = BadQualityConfigDomainModel.LatencyConfig(
                    minLatencyMs = 100,
                    maxLatencyMs = 400,
                    triggerProbability = 0.8
                ),
                errorProbability = 0.0,
                errors = emptyList(),
            ),
            BadQualityConfigDomainModel(
                id = idPrefix + "medium_latency",
                isEnabled = false,
                name = "Medium latency",
                createdAt = Instant.fromEpochMilliseconds(now + 2),
                latency = BadQualityConfigDomainModel.LatencyConfig(
                    minLatencyMs = 2000,
                    maxLatencyMs = 4000,
                    triggerProbability = 0.8
                ),
                errorProbability = 0.0,
                errors = emptyList(),
            ),
            BadQualityConfigDomainModel(
                id = idPrefix + "high_latency",
                isEnabled = false,
                name = "High latency",
                createdAt = Instant.fromEpochMilliseconds(now + 3),
                latency = BadQualityConfigDomainModel.LatencyConfig(
                    minLatencyMs = 10_000,
                    maxLatencyMs = 30_000,
                    triggerProbability = 1.0,
                ),
                errorProbability = 0.0,
                errors = emptyList(),
            ),
            BadQualityConfigDomainModel(
                id = idPrefix + "timeout",
                isEnabled = false,
                name = "100% Timeout",
                createdAt = Instant.fromEpochMilliseconds(now + 4),
                latency = BadQualityConfigDomainModel.LatencyConfig(
                    minLatencyMs = 0,
                    maxLatencyMs = 0,
                    triggerProbability = 0.0,
                ),
                errorProbability = 1.0,
                errors = listOf(
                    BadQualityConfigDomainModel.Error(
                        weight = 1.0f,
                        type = BadQualityConfigDomainModel.Error.Type.Exception(
                            classPath = "java.net.SocketTimeoutException"
                        )
                    )
                ),
            ),
            BadQualityConfigDomainModel(
                id = idPrefix + "server_down",
                isEnabled = false,
                name = "Server Down",
                createdAt = Instant.fromEpochMilliseconds(now + 5),
                latency = BadQualityConfigDomainModel.LatencyConfig(
                    minLatencyMs = 0,
                    maxLatencyMs = 0,
                    triggerProbability = 0.0,
                ),
                errorProbability = 1.0,
                errors = listOf(
                    BadQualityConfigDomainModel.Error(
                        weight = 1.0f,
                        type = BadQualityConfigDomainModel.Error.Type.Body(
                            httpCode = 500,
                            body = "{}",
                            contentType = "application/json"
                        )
                    )
                ),
            ),
        )
    }
}
