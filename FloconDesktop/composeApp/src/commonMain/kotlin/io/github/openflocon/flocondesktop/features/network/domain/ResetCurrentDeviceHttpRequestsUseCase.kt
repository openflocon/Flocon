package io.github.openflocon.flocondesktop.features.network.domain

import io.github.openflocon.flocondesktop.core.domain.device.GetCurrentDeviceIdUseCase
import io.github.openflocon.flocondesktop.features.network.domain.repository.NetworkRepository

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
