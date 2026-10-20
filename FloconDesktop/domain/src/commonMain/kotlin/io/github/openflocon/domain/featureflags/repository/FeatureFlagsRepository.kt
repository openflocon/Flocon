package io.github.openflocon.domain.featureflags.repository

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.featureflags.models.FeatureFlagDomainModel
import kotlinx.coroutines.flow.Flow

interface FeatureFlagsRepository {

    fun observeFeatureFlags(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        provider: String,
        filter: String?,
    ): Flow<List<FeatureFlagDomainModel>>

    fun observeFeatureFlag(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        provider: String,
        name: String,
    ): Flow<FeatureFlagDomainModel?>

    fun observeProviders(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): Flow<List<String>>

    fun observeSelectedProvider(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): Flow<String?>

    suspend fun selectProvider(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        provider: String,
    )

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
