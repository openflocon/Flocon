package io.github.openflocon.flocondesktop.features.network.domain

import io.github.openflocon.flocondesktop.core.domain.device.GetCurrentDeviceIdUseCase
import io.github.openflocon.flocondesktop.features.network.domain.repository.NetworkRepository

class RemoveHttpRequestsBeforeUseCase(
    private val networkRepository: NetworkRepository,
    private val getCurrentDeviceIdUseCase: GetCurrentDeviceIdUseCase,
) {
    suspend operator fun invoke(requestId: String) {
        getCurrentDeviceIdUseCase()?.let { deviceId ->
            networkRepository.deleteRequestsBefore(
                deviceId = deviceId,
                requestId = requestId,
            )
        }
    }
}
