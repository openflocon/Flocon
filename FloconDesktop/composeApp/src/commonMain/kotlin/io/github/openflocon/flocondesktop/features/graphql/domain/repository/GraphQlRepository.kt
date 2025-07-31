package io.github.openflocon.flocondesktop.features.graphql.domain.repository

import io.github.openflocon.flocondesktop.DeviceId
import io.github.openflocon.flocondesktop.features.graphql.domain.model.GraphQlRequestDomainModel
import io.github.openflocon.flocondesktop.features.graphql.domain.model.GraphQlRequestId
import kotlinx.coroutines.flow.Flow

interface GraphQlRepository {
    fun observeRequests(deviceId: DeviceId): Flow<List<GraphQlRequestDomainModel>>
    fun observeRequest(currentDeviceId: DeviceId, requestId: GraphQlRequestId): Flow<GraphQlRequestDomainModel?>

    suspend fun deleteRequest(deviceId: DeviceId, requestId: GraphQlRequestId)
    suspend fun deleteRequestsBefore(deviceId: DeviceId, requestId: GraphQlRequestId)
    suspend fun deleteRequestsForDevice(deviceId: DeviceId)
}
