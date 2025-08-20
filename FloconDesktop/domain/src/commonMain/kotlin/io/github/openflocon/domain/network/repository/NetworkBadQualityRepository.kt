package io.github.openflocon.domain.network.repository

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.network.models.BadQualityConfigDomainModel
import io.github.openflocon.domain.network.models.BadQualityConfigId
import kotlinx.coroutines.flow.Flow

interface NetworkBadQualityRepository {
    // send to device
    suspend fun setupBadNetworkQuality(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        config: BadQualityConfigDomainModel?
    )

    // save locally
    suspend fun saveBadNetworkQuality(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        config: BadQualityConfigDomainModel,
    )

    suspend fun getNetworkQuality(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        configId: BadQualityConfigId,
    ) : BadQualityConfigDomainModel?

    suspend fun getTheOnlyEnabledNetworkQuality(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ) : BadQualityConfigDomainModel?

    suspend fun deleteNetworkQuality(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        configId: BadQualityConfigId,
    )

    fun observeNetworkQuality(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        configId: BadQualityConfigId,
    ) : Flow<BadQualityConfigDomainModel?>

    fun observeAllNetworkQualities(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ) : Flow<List<BadQualityConfigDomainModel>>

    suspend fun setEnabledConfig(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        configId: BadQualityConfigId?,
    )
}
