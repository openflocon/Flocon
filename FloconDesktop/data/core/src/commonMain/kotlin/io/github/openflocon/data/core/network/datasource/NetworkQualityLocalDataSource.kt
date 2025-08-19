package io.github.openflocon.data.core.network.datasource

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.network.models.BadQualityConfigDomainModel
import kotlinx.coroutines.flow.Flow

interface NetworkQualityLocalDataSource {
    suspend fun save(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        config: BadQualityConfigDomainModel
    )

    suspend fun getNetworkQuality(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel) : BadQualityConfigDomainModel?

    fun observe(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
    ): Flow<BadQualityConfigDomainModel?>

    suspend fun updateIsEnabled(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        isEnabled: Boolean,
    )

}
