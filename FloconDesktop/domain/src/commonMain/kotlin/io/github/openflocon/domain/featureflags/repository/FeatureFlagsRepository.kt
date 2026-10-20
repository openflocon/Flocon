package io.github.openflocon.domain.featureflags.repository

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.featureflags.models.FeatureFlagDomainModel
import kotlinx.coroutines.flow.Flow

interface FeatureFlagsRepository {

    fun observeFeatureFlags(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        filter: String?,
    ): Flow<List<FeatureFlagDomainModel>>

    fun observeFeatureFlag(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        provider: String,
        name: String,
    ): Flow<FeatureFlagDomainModel?>

    suspend fun searchFeatureFlags(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        provider: String?,
        filter: String?,
        limit: Int,
    ): List<FeatureFlagDomainModel>

    suspend fun getFeatureFlag(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        provider: String,
        name: String,
    ): FeatureFlagDomainModel?

    suspend fun getProviders(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): List<String>
}
