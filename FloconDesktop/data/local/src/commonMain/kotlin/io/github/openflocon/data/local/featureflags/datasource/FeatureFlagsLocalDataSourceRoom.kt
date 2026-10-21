package io.github.openflocon.data.local.featureflags.datasource

import io.github.openflocon.data.core.featureflags.datasource.FeatureFlagsLocalDataSource
import io.github.openflocon.data.local.featureflags.dao.FeatureFlagDao
import io.github.openflocon.data.local.featureflags.models.FeatureFlagEntity
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.featureflags.models.FeatureFlagDomainModel
import io.github.openflocon.domain.featureflags.models.FeatureFlagStateDomainModel
import io.github.openflocon.domain.featureflags.models.FeatureFlagsProviderDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class FeatureFlagsLocalDataSourceRoom(
    private val featureFlagDao: FeatureFlagDao,
) : FeatureFlagsLocalDataSource {

    override suspend fun replace(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        featureFlags: FeatureFlagsProviderDomainModel,
    ) {
        featureFlagDao.replaceProvider(
            deviceId = deviceIdAndPackageName.deviceId,
            packageName = deviceIdAndPackageName.packageName,
            provider = featureFlags.provider,
            appInstance = deviceIdAndPackageName.appInstance,
            entities = featureFlags.flags.map {
                it.toEntity(deviceIdAndPackageName)
            },
        )
    }

    override fun observe(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        provider: String,
        filter: String?,
    ): Flow<List<FeatureFlagDomainModel>> = featureFlagDao.observeFeatureFlags(
        deviceId = deviceIdAndPackageName.deviceId,
        packageName = deviceIdAndPackageName.packageName,
        provider = provider,
        filter = filter?.takeIf { it.isNotBlank() },
    )
        .map { list -> list.map { it.toDomain() } }
        .distinctUntilChanged()

    override fun observeProviders(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): Flow<List<String>> = featureFlagDao.observeProviders(
        deviceId = deviceIdAndPackageName.deviceId,
        packageName = deviceIdAndPackageName.packageName,
    ).distinctUntilChanged()

    override fun observeFeatureFlag(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        provider: String,
        name: String,
    ): Flow<FeatureFlagDomainModel?> = featureFlagDao.observeFeatureFlag(
        deviceId = deviceIdAndPackageName.deviceId,
        packageName = deviceIdAndPackageName.packageName,
        provider = provider,
        name = name,
    )
        .map { it?.toDomain() }
        .distinctUntilChanged()

    override suspend fun search(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        provider: String?,
        filter: String?,
        limit: Int,
    ): List<FeatureFlagDomainModel> = featureFlagDao.searchFeatureFlags(
        deviceId = deviceIdAndPackageName.deviceId,
        packageName = deviceIdAndPackageName.packageName,
        provider = provider,
        filter = filter?.takeIf { it.isNotBlank() },
        limit = limit,
    ).map { it.toDomain() }

    override suspend fun get(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        provider: String,
        name: String,
    ): FeatureFlagDomainModel? = featureFlagDao.getFeatureFlag(
        deviceId = deviceIdAndPackageName.deviceId,
        packageName = deviceIdAndPackageName.packageName,
        provider = provider,
        name = name,
    )?.toDomain()

    override suspend fun getProviders(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): List<String> = featureFlagDao.getProviders(
        deviceId = deviceIdAndPackageName.deviceId,
        packageName = deviceIdAndPackageName.packageName,
    )
}

private fun FeatureFlagDomainModel.toEntity(
    deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
) = FeatureFlagEntity(
    deviceId = deviceIdAndPackageName.deviceId,
    packageName = deviceIdAndPackageName.packageName,
    provider = provider,
    name = name,
    state = state.name,
    variant = variant,
    payload = payload,
    appInstance = deviceIdAndPackageName.appInstance,
)

private fun FeatureFlagEntity.toDomain() = FeatureFlagDomainModel(
    provider = provider,
    name = name,
    state = FeatureFlagStateDomainModel.entries.firstOrNull { it.name == state } ?: FeatureFlagStateDomainModel.Custom,
    variant = variant,
    payload = payload,
)
