package io.github.openflocon.domain.network.repository

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.network.models.NetworkSettingsDomainModel
import kotlinx.coroutines.flow.Flow

interface NetworkSettingsRepository {
    suspend fun getNetworkSettings(
        deviceAndApp: DeviceIdAndPackageNameDomainModel,
    ): NetworkSettingsDomainModel?

    fun observeNetworkSettings(deviceAndApp: DeviceIdAndPackageNameDomainModel): Flow<NetworkSettingsDomainModel?>

    suspend fun updateNetworkSettings(
        deviceAndApp: DeviceIdAndPackageNameDomainModel,
        newValue: NetworkSettingsDomainModel,
    )
}
