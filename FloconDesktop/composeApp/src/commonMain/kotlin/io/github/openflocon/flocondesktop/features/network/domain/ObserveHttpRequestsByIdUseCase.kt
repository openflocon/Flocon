package io.github.openflocon.flocondesktop.features.network.domain

import com.florent37.flocondesktop.core.domain.device.ObserveCurrentDeviceIdUseCase
import com.florent37.flocondesktop.features.network.domain.model.FloconHttpRequestDomainModel
import com.florent37.flocondesktop.features.network.domain.repository.NetworkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveHttpRequestsByIdUseCase(
    private val networkRepository: NetworkRepository,
    private val observeCurrentDeviceIdUseCase: ObserveCurrentDeviceIdUseCase,
) {
    operator fun invoke(requestId: String): Flow<FloconHttpRequestDomainModel?> = observeCurrentDeviceIdUseCase()
        .flatMapLatest { deviceId ->
            if (deviceId == null) {
                flowOf(null)
            } else {
                networkRepository.observeRequest(deviceId = deviceId, requestId = requestId)
            }
        }.distinctUntilChanged()
}
