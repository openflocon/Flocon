package com.florent37.flocondesktop.features.graphql.domain

import com.florent37.flocondesktop.core.domain.device.GetCurrentDeviceIdUseCase
import com.florent37.flocondesktop.features.graphql.domain.repository.GraphQlRepository

class ResetCurrentDeviceGraphQlRequestsUseCase(
    private val graphQlRepository: GraphQlRepository,
    private val getCurrentDeviceIdUseCase: GetCurrentDeviceIdUseCase,
) {
    suspend operator fun invoke() {
        val deviceId = getCurrentDeviceIdUseCase() ?: return
        graphQlRepository.deleteRequestsForDevice(deviceId = deviceId)
    }
}
