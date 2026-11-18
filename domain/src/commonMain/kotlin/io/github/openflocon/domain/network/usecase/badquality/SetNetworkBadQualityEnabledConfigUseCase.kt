package io.github.openflocon.domain.network.usecase.badquality

import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.network.models.BadQualityConfigId
import io.github.openflocon.domain.network.repository.NetworkBadQualityRepository

class SetNetworkBadQualityEnabledConfigUseCase(
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val networkBadQualityRepository: NetworkBadQualityRepository,
    private val setupNetworkBadQualityUseCase: SetupNetworkBadQualityUseCase,
) {
    suspend operator fun invoke(
        configId: BadQualityConfigId?, // null to enable none
    ) {
        getCurrentDeviceIdAndPackageNameUseCase()?.let { deviceIdAndPackageName ->
            networkBadQualityRepository.setEnabledConfig(
                configId = configId,
                deviceIdAndPackageName = deviceIdAndPackageName,
            )
            // then send to device
            setupNetworkBadQualityUseCase()
        }
    }
}
