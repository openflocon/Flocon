package io.github.openflocon.flocondesktop.features.graphql.domain

import io.github.openflocon.flocondesktop.core.domain.device.GetCurrentDeviceIdUseCase
import io.github.openflocon.flocondesktop.features.graphql.domain.repository.GraphQlRepository

class ResetCurrentDeviceGraphQlRequestsUseCase(
    private val graphQlRepository: GraphQlRepository,
    private val getCurrentDeviceIdUseCase: GetCurrentDeviceIdUseCase,
) {
    suspend operator fun invoke() {
        val deviceId = getCurrentDeviceIdUseCase() ?: return
        graphQlRepository.deleteRequestsForDevice(deviceId = deviceId)
    }
}
