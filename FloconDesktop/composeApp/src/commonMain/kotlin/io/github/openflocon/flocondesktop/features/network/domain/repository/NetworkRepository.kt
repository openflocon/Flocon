package io.github.openflocon.flocondesktop.features.network.domain.repository

import io.github.openflocon.flocondesktop.DeviceId
import io.github.openflocon.flocondesktop.features.network.domain.model.FloconHttpRequestDomainModel
import kotlinx.coroutines.flow.Flow

interface NetworkRepository {
    fun observeRequests(
        deviceId: String,
        packageName: String
    ): Flow<List<FloconHttpRequestDomainModel>>

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
