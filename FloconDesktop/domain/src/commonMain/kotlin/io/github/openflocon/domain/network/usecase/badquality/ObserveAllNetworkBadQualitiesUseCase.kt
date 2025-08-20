package io.github.openflocon.domain.network.usecase.badquality

import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.network.models.BadQualityConfigDomainModel
import io.github.openflocon.domain.network.repository.NetworkBadQualityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveAllNetworkBadQualitiesUseCase(
    private val networkBadQualityRepository: NetworkBadQualityRepository,
    private val observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
) {
    operator fun invoke(): Flow<List<BadQualityConfigDomainModel>> =
        observeCurrentDeviceIdAndPackageNameUseCase()
            .flatMapLatest { current ->
                if (current == null) {
                    flowOf(emptyList())
                } else {
                    networkBadQualityRepository.observeAllNetworkQualities(
                        deviceIdAndPackageName = current,
                    )
                }
            }
            .distinctUntilChanged()
}
