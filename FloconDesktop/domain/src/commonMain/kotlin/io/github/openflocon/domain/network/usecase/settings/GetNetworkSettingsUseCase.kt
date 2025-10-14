package io.github.openflocon.domain.network.usecase.settings

import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.network.models.MockNetworkDomainModel
import io.github.openflocon.domain.network.models.NetworkSettingsDomainModel
import io.github.openflocon.domain.network.repository.NetworkMocksRepository
import io.github.openflocon.domain.network.repository.NetworkSettingsRepository
import io.github.openflocon.domain.network.usecase.mocks.SetupNetworkMocksUseCase

class GetNetworkSettingsUseCase(
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val networkSettingsRepository: NetworkSettingsRepository,
) {
    suspend operator fun invoke() : NetworkSettingsDomainModel? {
        return getCurrentDeviceIdAndPackageNameUseCase()?.let { deviceIdAndPackageName ->
            networkSettingsRepository.getNetworkSettings(
                deviceAndApp = deviceIdAndPackageName,
            )
        }
    }
}
