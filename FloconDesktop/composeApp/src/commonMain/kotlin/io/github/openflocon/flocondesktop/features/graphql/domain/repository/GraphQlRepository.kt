package io.github.openflocon.flocondesktop.features.graphql.domain.repository

import com.florent37.flocondesktop.DeviceId
import com.florent37.flocondesktop.features.graphql.domain.model.GraphQlRequestDomainModel
import com.florent37.flocondesktop.features.graphql.domain.model.GraphQlRequestId
import kotlinx.coroutines.flow.Flow

interface GraphQlRepository {
    fun observeRequests(deviceId: DeviceId): Flow<List<GraphQlRequestDomainModel>>
    fun observeRequest(currentDeviceId: DeviceId, requestId: GraphQlRequestId): Flow<GraphQlRequestDomainModel?>

    suspend fun deleteRequest(deviceId: DeviceId, requestId: GraphQlRequestId)
    suspend fun deleteRequestsBefore(deviceId: DeviceId, requestId: GraphQlRequestId)
    suspend fun deleteRequestsForDevice(deviceId: DeviceId)
}
