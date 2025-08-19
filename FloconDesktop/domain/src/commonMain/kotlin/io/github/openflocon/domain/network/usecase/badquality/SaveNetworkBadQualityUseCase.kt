package io.github.openflocon.domain.network.usecase.badquality

import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.network.models.BadQualityConfigDomainModel
import io.github.openflocon.domain.network.repository.NetworkBadQualityRepository
import io.github.openflocon.domain.network.repository.NetworkMocksRepository

class SaveNetworkBadQualityUseCase(
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val networkBadQualityRepository: NetworkBadQualityRepository,
    private val setupNetworkBadQualityUseCase: SetupNetworkBadQualityUseCase,
) {
    suspend operator fun invoke(config: BadQualityConfigDomainModel) {
        getCurrentDeviceIdAndPackageNameUseCase()?.let { deviceIdAndPackageName ->
            networkBadQualityRepository.saveBadNetworkQuality(
                config = config,
                deviceIdAndPackageName = deviceIdAndPackageName,
            )
            // then send to device
            setupNetworkBadQualityUseCase()
        }
    }
}
