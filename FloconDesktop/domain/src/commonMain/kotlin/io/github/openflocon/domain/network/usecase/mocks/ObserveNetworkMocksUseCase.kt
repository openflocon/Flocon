package io.github.openflocon.domain.network.usecase.mocks

import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.network.models.MockNetworkDomainModel
import io.github.openflocon.domain.network.repository.NetworkMocksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveNetworkMocksUseCase(
    private val networkMocksRepository: NetworkMocksRepository,
    private val observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
) {
    operator fun invoke(): Flow<List<MockNetworkDomainModel>> =
        observeCurrentDeviceIdAndPackageNameUseCase()
            .flatMapLatest { current ->
                if (current == null) {
                    flowOf(emptyList())
                } else {
                    networkMocksRepository.observeAll(deviceIdAndPackageName = current)
                }
            }
            .distinctUntilChanged()
}
