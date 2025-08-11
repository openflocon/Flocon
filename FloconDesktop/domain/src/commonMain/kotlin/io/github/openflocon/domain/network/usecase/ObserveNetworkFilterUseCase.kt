package io.github.openflocon.domain.network.usecase

import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdUseCase
import io.github.openflocon.domain.models.TextFilterStateDomainModel
import io.github.openflocon.domain.network.models.NetworkTextFilterColumns
import io.github.openflocon.domain.network.repository.NetworkFilterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveNetworkFilterUseCase(
    private val observeCurrentDeviceIdUseCase: ObserveCurrentDeviceIdUseCase,
    private val networkFilterRepository: NetworkFilterRepository,
) {
    operator fun invoke(): Flow<Map<NetworkTextFilterColumns, TextFilterStateDomainModel>> =
        observeCurrentDeviceIdUseCase().flatMapLatest { deviceId ->
            if (deviceId == null) flowOf(emptyMap())
            else networkFilterRepository.observe(deviceId = deviceId)
        }.distinctUntilChanged()
}
