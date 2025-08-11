package io.github.openflocon.domain.network.repository

import io.github.openflocon.domain.device.models.DeviceId
import io.github.openflocon.domain.network.models.FloconHttpRequestDomainModel
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow

interface NetworkRepository {
    // lite : exclude headers, sizes, body
    fun observeRequests(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, lite: Boolean): Flow<List<FloconHttpRequestDomainModel>>

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
