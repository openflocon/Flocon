package io.github.openflocon.flocondesktop.features.network.domain.filter

import io.github.openflocon.flocondesktop.core.domain.device.ObserveCurrentDeviceIdUseCase
import io.github.openflocon.flocondesktop.features.network.domain.model.NetworkTextFilterColumns
import io.github.openflocon.flocondesktop.features.network.domain.model.TextFilterStateDomainModel
import io.github.openflocon.flocondesktop.features.network.domain.repository.NetworkFilterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveNetworkFilterUseCase(
    private val observeCurrentDeviceIdUseCase: ObserveCurrentDeviceIdUseCase,
    private val networkFilterRepository: NetworkFilterRepository,
) {
    operator fun invoke(): Flow<Map<NetworkTextFilterColumns, TextFilterStateDomainModel>> {
        return observeCurrentDeviceIdUseCase().flatMapLatest { deviceId ->
            if (deviceId == null) flowOf(emptyMap())
            else networkFilterRepository.observe(deviceId = deviceId)
        }.distinctUntilChanged()
    }
}
