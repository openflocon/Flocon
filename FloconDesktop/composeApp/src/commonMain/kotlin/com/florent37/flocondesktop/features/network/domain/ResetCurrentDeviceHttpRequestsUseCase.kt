package com.florent37.flocondesktop.features.network.domain

import com.florent37.flocondesktop.core.domain.device.GetCurrentDeviceIdUseCase
import com.florent37.flocondesktop.features.network.domain.repository.NetworkRepository

class ResetCurrentDeviceHttpRequestsUseCase(
    private val networkRepository: NetworkRepository,
    private val getCurrentDeviceIdUseCase: GetCurrentDeviceIdUseCase,
) {
    suspend operator fun invoke() {
        getCurrentDeviceIdUseCase()?.let {
            networkRepository.clearDeviceCalls(deviceId = it)
        }
    }
}
