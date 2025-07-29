package com.florent37.flocondesktop.features.network.domain

import com.florent37.flocondesktop.core.domain.device.GetCurrentDeviceIdUseCase
import com.florent37.flocondesktop.features.network.domain.repository.NetworkRepository

class RemoveHttpRequestUseCase(
    private val networkRepository: NetworkRepository,
    private val getCurrentDeviceIdUseCase: GetCurrentDeviceIdUseCase,
) {
    suspend operator fun invoke(requestId: String) {
        getCurrentDeviceIdUseCase()?.let { deviceId ->
            networkRepository.deleteRequest(deviceId = deviceId, requestId = requestId)
        }
    }
}
