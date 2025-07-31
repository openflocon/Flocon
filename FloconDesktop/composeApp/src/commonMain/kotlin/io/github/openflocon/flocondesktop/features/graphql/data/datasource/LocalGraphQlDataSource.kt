package io.github.openflocon.flocondesktop.features.graphql.data.datasource

import com.florent37.flocondesktop.DeviceId
import com.florent37.flocondesktop.features.graphql.domain.model.GraphQlRequestDomainModel
import com.florent37.flocondesktop.features.graphql.domain.model.GraphQlRequestId
import kotlinx.coroutines.flow.Flow

interface LocalGraphQlDataSource {
    suspend fun insert(
        deviceId: DeviceId,
        request: GraphQlRequestDomainModel,
    )

    fun observeRequests(deviceId: DeviceId): Flow<List<GraphQlRequestDomainModel>>
    fun observeRequest(deviceId: DeviceId, requestId: GraphQlRequestId): Flow<GraphQlRequestDomainModel?>
    suspend fun clearDeviceCalls(deviceId: DeviceId)

    suspend fun deleteRequest(deviceId: DeviceId, requestId: GraphQlRequestId)
    suspend fun deleteRequestsBefore(deviceId: DeviceId, requestId: GraphQlRequestId)

    suspend fun clear()
}
