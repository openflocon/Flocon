package io.github.openflocon.domain.network.usecase.badquality

import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.network.models.BadQualityConfigDomainModel
import io.github.openflocon.domain.network.models.BadQualityConfigId
import io.github.openflocon.domain.network.repository.NetworkBadQualityRepository
import io.github.openflocon.domain.network.repository.NetworkMocksRepository

class DeleteBadQualityUseCase(
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val networkBadQualityRepository: NetworkBadQualityRepository,
    private val setupNetworkBadQualityUseCase: SetupNetworkBadQualityUseCase,
) {
    suspend operator fun invoke(configId: BadQualityConfigId) {
        getCurrentDeviceIdAndPackageNameUseCase()?.let { deviceIdAndPackageName ->
            networkBadQualityRepository.deleteNetworkQuality(
                configId = configId,
                deviceIdAndPackageName = deviceIdAndPackageName,
            )
            // then send to device
            setupNetworkBadQualityUseCase()
        }
    }
}
