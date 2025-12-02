package io.github.openflocon.domain.network.usecase.badquality

import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.network.repository.NetworkBadQualityRepository

class SetupNetworkBadQualityUseCase(
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val networkBadQualityRepository: NetworkBadQualityRepository,
) {
    suspend operator fun invoke() {
        getCurrentDeviceIdAndPackageNameUseCase()?.let { deviceIdAndPackageName ->
            networkBadQualityRepository.setupBadNetworkQuality(
                config = networkBadQualityRepository.getTheOnlyEnabledNetworkQuality(
                    deviceIdAndPackageName = deviceIdAndPackageName,
                ),
                deviceIdAndPackageName = deviceIdAndPackageName,
            )
        }
    }
}
