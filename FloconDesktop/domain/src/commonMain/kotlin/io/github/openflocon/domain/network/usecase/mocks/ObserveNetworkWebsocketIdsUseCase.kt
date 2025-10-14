package io.github.openflocon.domain.network.usecase.mocks

import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdUseCase
import io.github.openflocon.domain.models.TextFilterStateDomainModel
import io.github.openflocon.domain.network.models.NetworkTextFilterColumns
import io.github.openflocon.domain.network.models.NetworkWebsocketId
import io.github.openflocon.domain.network.repository.NetworkFilterRepository
import io.github.openflocon.domain.network.repository.NetworkMocksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveNetworkWebsocketIdsUseCase(
    private val observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
    private val networkMocksRepository: NetworkMocksRepository,
) {
    operator fun invoke(): Flow<List<NetworkWebsocketId>> =
        observeCurrentDeviceIdAndPackageNameUseCase().flatMapLatest { current ->
            if (current == null) flowOf(emptyList())
            else networkMocksRepository.observeWebsocketClientsIds(deviceIdAndPackageName = current)
        }.distinctUntilChanged()
}
