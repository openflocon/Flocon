package io.github.openflocon.flocondesktop.features.network.domain

import io.github.openflocon.flocondesktop.core.domain.device.ObserveCurrentDeviceIdUseCase
import io.github.openflocon.flocondesktop.features.network.domain.model.FloconHttpRequestDomainModel
import io.github.openflocon.flocondesktop.features.network.domain.repository.NetworkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveHttpRequestsUseCase(
    private val networkRepository: NetworkRepository,
    private val observeCurrentDeviceIdUseCase: ObserveCurrentDeviceIdUseCase,
) {
    // lite : exclude headers, sizes, body
    operator fun invoke(lite: Boolean): Flow<List<FloconHttpRequestDomainModel>> = observeCurrentDeviceIdUseCase()
        .flatMapLatest { deviceId ->
            if (deviceId == null) {
                flowOf(emptyList())
            } else {
                networkRepository.observeRequests(deviceId = deviceId, lite = lite)
            }
        }.distinctUntilChanged()
}
