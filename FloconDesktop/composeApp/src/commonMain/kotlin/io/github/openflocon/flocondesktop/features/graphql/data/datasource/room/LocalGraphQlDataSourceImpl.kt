package io.github.openflocon.flocondesktop.features.graphql.data.datasource.room

import io.github.openflocon.flocondesktop.DeviceId
import io.github.openflocon.flocondesktop.features.graphql.data.datasource.LocalGraphQlDataSource
import io.github.openflocon.flocondesktop.features.graphql.data.datasource.room.mapper.toDomainModel
import io.github.openflocon.flocondesktop.features.graphql.data.datasource.room.mapper.toEntity
import io.github.openflocon.flocondesktop.features.graphql.domain.model.GraphQlRequestDomainModel
import io.github.openflocon.flocondesktop.features.graphql.domain.model.GraphQlRequestId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalGraphQlDataSourceImpl(
    private val graphQlDao: GraphQlDao,
) : LocalGraphQlDataSource {

    override suspend fun insert(
        deviceId: String,
        request: GraphQlRequestDomainModel,
    ) {
        graphQlDao.insertGraphQlCall(request.toEntity(deviceId = deviceId))
    }

    override fun observeRequests(deviceId: String): Flow<List<GraphQlRequestDomainModel>> = graphQlDao.observeRequests(deviceId = deviceId).map { entities ->
        entities.map { it.toDomainModel() }
    }

    override fun observeRequest(deviceId: DeviceId, requestId: GraphQlRequestId) = graphQlDao.observeRequest(deviceId = deviceId, requestId = requestId).map {
        it?.toDomainModel()
    }

    override suspend fun clearDeviceCalls(deviceId: String) {
        graphQlDao.clearDeviceRequests(deviceId)
    }

    override suspend fun deleteRequest(deviceId: String, requestId: GraphQlRequestId) {
        graphQlDao.deleteRequestById(requestId)
    }

    override suspend fun deleteRequestsBefore(deviceId: String, requestId: GraphQlRequestId) {
        val timestamp = graphQlDao.getRequestTimestamp(deviceId = deviceId, requestId = requestId) ?: return
        graphQlDao.deleteRequestsBeforeTimestamp(
            deviceId = deviceId,
            timestamp = timestamp,
        )
    }

    override suspend fun clear() {
        graphQlDao.clearAllData()
    }
}
