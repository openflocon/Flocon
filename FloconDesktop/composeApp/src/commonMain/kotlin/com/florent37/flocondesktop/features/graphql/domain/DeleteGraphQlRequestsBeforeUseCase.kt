package com.florent37.flocondesktop.features.graphql.domain

import com.florent37.flocondesktop.core.domain.device.GetCurrentDeviceIdUseCase
import com.florent37.flocondesktop.features.graphql.domain.model.GraphQlRequestId
import com.florent37.flocondesktop.features.graphql.domain.repository.GraphQlRepository

class DeleteGraphQlRequestsBeforeUseCase(
    private val graphQlRepository: GraphQlRepository,
    private val getCurrentDeviceIdUseCase: GetCurrentDeviceIdUseCase,
) {
    suspend operator fun invoke(requestId: GraphQlRequestId) {
        val deviceId = getCurrentDeviceIdUseCase() ?: return
        graphQlRepository.deleteRequestsBefore(deviceId = deviceId, requestId = requestId)
    }
}
