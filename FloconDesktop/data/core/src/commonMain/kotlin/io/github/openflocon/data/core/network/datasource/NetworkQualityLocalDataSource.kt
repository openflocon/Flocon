package io.github.openflocon.data.core.network.datasource

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.network.models.BadQualityConfigDomainModel
import io.github.openflocon.domain.network.models.BadQualityConfigId
import kotlinx.coroutines.flow.Flow

interface NetworkQualityLocalDataSource {
    suspend fun save(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        config: BadQualityConfigDomainModel
    )

    suspend fun getNetworkQuality(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        configId: BadQualityConfigId,
    ) : BadQualityConfigDomainModel?

    fun observe(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        configId: BadQualityConfigId,
    ): Flow<BadQualityConfigDomainModel?>

    suspend fun delete(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        configId: BadQualityConfigId,
    )

    fun observeAll(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): Flow<List<BadQualityConfigDomainModel>>

    suspend fun setEnabledConfig(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        configId: BadQualityConfigId?,
    )

    suspend fun getTheOnlyEnabledNetworkQuality(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel
    ): BadQualityConfigDomainModel?

}
