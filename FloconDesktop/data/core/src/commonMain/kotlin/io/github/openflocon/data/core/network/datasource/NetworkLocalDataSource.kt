package io.github.openflocon.data.core.network.datasource

import io.github.openflocon.domain.device.models.DeviceId
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import kotlinx.coroutines.flow.Flow

interface NetworkLocalDataSource {

    fun observeRequests(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        lite: Boolean,
    ): Flow<List<FloconNetworkCallDomainModel>>

    fun observeRequest(
        deviceId: DeviceId,
        requestId: String,
    ): Flow<FloconNetworkCallDomainModel?>

    suspend fun save(
        deviceId: DeviceId,
        packageName: String,
        call: FloconNetworkCallDomainModel,
    )

    suspend fun clearDeviceCalls(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel)

    suspend fun deleteRequest(deviceId: DeviceId, requestId: String)

    suspend fun deleteRequestsBefore(deviceId: DeviceId, requestId: String)

    suspend fun clear()

}
