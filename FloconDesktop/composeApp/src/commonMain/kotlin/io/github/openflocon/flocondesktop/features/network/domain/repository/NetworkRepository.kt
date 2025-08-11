package io.github.openflocon.flocondesktop.features.network.domain.repository

import com.flocon.library.domain.models.DeviceId
import com.flocon.library.domain.models.FloconHttpRequestDomainModel
import com.flocon.library.domain.models.DeviceIdAndPackageNameDomainModel
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
