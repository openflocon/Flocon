package io.github.openflocon.domain.network.repository

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.network.models.BadQualityConfigDomainModel
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
    ) : BadQualityConfigDomainModel?

    fun observeNetworkQuality(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ) : Flow<BadQualityConfigDomainModel?>

    suspend fun setNetworkQualityIsEnabled(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        isEnabled: Boolean,
    )
}
