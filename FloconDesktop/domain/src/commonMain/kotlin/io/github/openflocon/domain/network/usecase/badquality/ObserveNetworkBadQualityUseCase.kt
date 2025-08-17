package io.github.openflocon.domain.network.usecase.badquality

import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.network.models.BadQualityConfigDomainModel
import io.github.openflocon.domain.network.models.MockNetworkDomainModel
import io.github.openflocon.domain.network.repository.NetworkBadQualityRepository
import io.github.openflocon.domain.network.repository.NetworkMocksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveNetworkBadQualityUseCase(
    private val networkBadQualityRepository: NetworkBadQualityRepository,
    private val observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
) {
    operator fun invoke(): Flow<BadQualityConfigDomainModel?> =
        observeCurrentDeviceIdAndPackageNameUseCase()
            .flatMapLatest { current ->
                if (current == null) {
                    flowOf(null)
                } else {
                    networkBadQualityRepository.observeNetworkQuality(deviceIdAndPackageName = current)
                }
            }
            .distinctUntilChanged()
}
