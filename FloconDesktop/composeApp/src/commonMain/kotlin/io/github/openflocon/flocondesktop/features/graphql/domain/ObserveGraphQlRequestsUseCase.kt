package io.github.openflocon.flocondesktop.features.graphql.domain

import com.florent37.flocondesktop.core.domain.device.ObserveCurrentDeviceIdUseCase
import com.florent37.flocondesktop.features.graphql.domain.model.GraphQlRequestDomainModel
import com.florent37.flocondesktop.features.graphql.domain.repository.GraphQlRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveGraphQlRequestsUseCase(
    private val graphQlRepository: GraphQlRepository,
    private val observeCurrentDeviceIdUseCase: ObserveCurrentDeviceIdUseCase,
) {
    operator fun invoke(): Flow<List<GraphQlRequestDomainModel>> = observeCurrentDeviceIdUseCase().flatMapLatest { currentDeviceId ->
        if (currentDeviceId == null) {
            flowOf(emptyList())
        } else {
            graphQlRepository.observeRequests(currentDeviceId)
        }
    }
}
