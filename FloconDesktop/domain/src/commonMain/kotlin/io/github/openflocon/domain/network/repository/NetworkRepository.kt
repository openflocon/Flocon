package io.github.openflocon.domain.network.repository

import io.github.openflocon.domain.device.models.DeviceId
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow

interface NetworkRepository {
    // lite : exclude headers, sizes, body
    fun observeRequests(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, lite: Boolean): Flow<List<FloconNetworkCallDomainModel>>

    fun observeRequest(
        deviceId: String,
        requestId: String,
    ): Flow<FloconNetworkCallDomainModel?>

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
