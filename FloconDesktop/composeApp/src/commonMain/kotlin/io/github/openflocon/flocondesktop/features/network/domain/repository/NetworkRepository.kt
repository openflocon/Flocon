package io.github.openflocon.flocondesktop.features.network.domain.repository

import io.github.openflocon.flocondesktop.DeviceId
import io.github.openflocon.flocondesktop.features.network.domain.model.FloconHttpRequestDomainModel
import io.github.openflocon.flocondesktop.messages.domain.model.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow

interface NetworkRepository {
    fun observeRequests(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel
    ): Flow<List<FloconHttpRequestDomainModel>>

    fun observeRequest(
        deviceId: String,
        requestId: String,
    ): Flow<FloconHttpRequestDomainModel?>

    suspend fun clear()
    suspend fun clearDeviceCalls(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel)

    suspend fun deleteRequest(
        deviceId: DeviceId,
        requestId: String,
    )

    suspend fun deleteRequestsBefore(
        deviceId: DeviceId,
        requestId: String,
    )
}
