package com.florent37.flocondesktop.features.network.domain

import com.florent37.flocondesktop.core.domain.device.ObserveCurrentDeviceIdUseCase
import com.florent37.flocondesktop.features.network.domain.model.FloconHttpRequestDomainModel
import com.florent37.flocondesktop.features.network.domain.repository.NetworkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveHttpRequestsUseCase(
    private val networkRepository: NetworkRepository,
    private val observeCurrentDeviceIdUseCase: ObserveCurrentDeviceIdUseCase,
) {
    operator fun invoke(): Flow<List<FloconHttpRequestDomainModel>> = observeCurrentDeviceIdUseCase()
        .flatMapLatest { deviceId ->
            if (deviceId == null) {
                flowOf(emptyList())
            } else {
                networkRepository.observeRequests(deviceId)
            }
        }.distinctUntilChanged()
}
