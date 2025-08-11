package io.github.openflocon.domain.network.usecase

import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdUseCase
import io.github.openflocon.domain.network.repository.NetworkRepository

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
