package io.github.openflocon.domain.network.usecase.badquality

import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.network.repository.NetworkBadQualityRepository
import io.github.openflocon.domain.network.repository.NetworkMocksRepository

class SetupNetworkBadQualityUseCase(
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val networkBadQualityRepository: NetworkBadQualityRepository,
) {
    suspend operator fun invoke() {
        getCurrentDeviceIdAndPackageNameUseCase()?.let { deviceIdAndPackageName ->
            networkBadQualityRepository.setupBadNetworkQuality(
                config = networkBadQualityRepository.getNetworkQuality(deviceIdAndPackageName),
                deviceIdAndPackageName = deviceIdAndPackageName,
            )
        }
    }
}
