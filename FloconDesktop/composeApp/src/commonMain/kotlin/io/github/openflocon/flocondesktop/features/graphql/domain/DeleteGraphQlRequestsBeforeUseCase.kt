package io.github.openflocon.flocondesktop.features.graphql.domain

import io.github.openflocon.flocondesktop.core.domain.device.GetCurrentDeviceIdUseCase
import io.github.openflocon.flocondesktop.features.graphql.domain.model.GraphQlRequestId
import io.github.openflocon.flocondesktop.features.graphql.domain.repository.GraphQlRepository

class DeleteGraphQlRequestsBeforeUseCase(
    private val graphQlRepository: GraphQlRepository,
    private val getCurrentDeviceIdUseCase: GetCurrentDeviceIdUseCase,
) {
    suspend operator fun invoke(requestId: GraphQlRequestId) {
        val deviceId = getCurrentDeviceIdUseCase() ?: return
        graphQlRepository.deleteRequestsBefore(deviceId = deviceId, requestId = requestId)
    }
}
