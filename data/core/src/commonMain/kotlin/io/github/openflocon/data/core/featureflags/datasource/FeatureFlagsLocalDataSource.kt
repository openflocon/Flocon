package io.github.openflocon.data.core.featureflags.datasource

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.featureflags.models.FeatureFlagDomainModel
import io.github.openflocon.domain.featureflags.models.FeatureFlagsProviderDomainModel
import kotlinx.coroutines.flow.Flow

interface FeatureFlagsLocalDataSource {

    /**
     * Replaces the flags of the provider, and drops the flags sent by a previous app session
     */
    suspend fun replace(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        featureFlags: FeatureFlagsProviderDomainModel,
    )

    fun observe(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        provider: String,
        filter: String?,
    ): Flow<List<FeatureFlagDomainModel>>

    fun observeProviders(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): Flow<List<String>>

    fun observeFeatureFlag(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        provider: String,
        name: String,
    ): Flow<FeatureFlagDomainModel?>

    suspend fun search(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        provider: String?,
        filter: String?,
        limit: Int,
    ): List<FeatureFlagDomainModel>

    suspend fun get(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        provider: String,
        name: String,
    ): FeatureFlagDomainModel?

    suspend fun getProviders(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): List<String>
}
