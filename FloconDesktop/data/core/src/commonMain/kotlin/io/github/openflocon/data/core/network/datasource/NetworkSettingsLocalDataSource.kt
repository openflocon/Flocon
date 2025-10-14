package io.github.openflocon.data.core.network.datasource

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.network.models.NetworkSettingsDomainModel
import kotlinx.coroutines.flow.Flow

interface NetworkSettingsLocalDataSource {
    suspend fun getNetworkSettings(
        deviceAndApp: DeviceIdAndPackageNameDomainModel,
    ): NetworkSettingsDomainModel?

    fun observeNetworkSettings(deviceAndApp: DeviceIdAndPackageNameDomainModel): Flow<NetworkSettingsDomainModel?>

    suspend fun updateNetworkSettings(
        deviceAndApp: DeviceIdAndPackageNameDomainModel,
        newValue: NetworkSettingsDomainModel,
    )
}
