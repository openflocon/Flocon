package io.github.openflocon.flocondesktop.features.network.data.datasource.local

import io.github.openflocon.flocondesktop.DeviceId
import io.github.openflocon.flocondesktop.features.network.domain.model.FloconHttpRequestDomainModel
import kotlinx.coroutines.flow.Flow

interface NetworkLocalDataSource {
    fun observeRequests(deviceId: DeviceId): Flow<List<FloconHttpRequestDomainModel>>

    fun observeRequest(
        deviceId: DeviceId,
        requestId: String,
    ): Flow<FloconHttpRequestDomainModel?>

    suspend fun save(
        deviceId: DeviceId,
        request: FloconHttpRequestDomainModel,
    )

    suspend fun clearDeviceCalls(deviceId: DeviceId)

    suspend fun deleteRequest(deviceId: DeviceId, requestId: String)
    suspend fun deleteRequestsBefore(deviceId: DeviceId, requestId: String)

    suspend fun clear()
}
