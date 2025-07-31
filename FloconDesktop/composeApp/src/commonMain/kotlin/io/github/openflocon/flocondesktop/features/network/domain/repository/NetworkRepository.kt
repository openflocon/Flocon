package io.github.openflocon.flocondesktop.features.network.domain.repository

import com.florent37.flocondesktop.DeviceId
import com.florent37.flocondesktop.features.network.domain.model.FloconHttpRequestDomainModel
import kotlinx.coroutines.flow.Flow

interface NetworkRepository {
    fun observeRequests(deviceId: String): Flow<List<FloconHttpRequestDomainModel>>

    fun observeRequest(
        deviceId: String,
        requestId: String,
    ): Flow<FloconHttpRequestDomainModel?>

    suspend fun clear()
    suspend fun clearDeviceCalls(deviceId: DeviceId)

    suspend fun deleteRequest(
        deviceId: DeviceId,
        requestId: String,
    )

    suspend fun deleteRequestsBefore(
        deviceId: DeviceId,
        requestId: String,
    )
}
