package io.github.openflocon.flocondesktop.features.network.data.datasource.local

import io.github.openflocon.domain.device.models.DeviceId
import io.github.openflocon.domain.network.models.FloconHttpRequestDomainModel
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import kotlinx.coroutines.flow.Flow

interface NetworkLocalDataSource {

    fun observeRequests(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        lite: Boolean,
    ): Flow<List<FloconHttpRequestDomainModel>>

    fun observeRequest(
        deviceId: DeviceId,
        requestId: String,
    ): Flow<FloconHttpRequestDomainModel?>

    suspend fun save(
        deviceId: DeviceId,
        packageName: String,
        request: FloconHttpRequestDomainModel,
    )

    suspend fun clearDeviceCalls(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel)

    suspend fun deleteRequest(deviceId: DeviceId, requestId: String)

    suspend fun deleteRequestsBefore(deviceId: DeviceId, requestId: String)

    suspend fun clear()

}
