package io.github.openflocon.data.core.network.datasource

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import kotlinx.coroutines.flow.Flow

interface NetworkLocalDataSource {

    fun observeRequests(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        lite: Boolean,
    ): Flow<List<FloconNetworkCallDomainModel>>

    suspend fun getCalls(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        ids: List<String>
    ): List<FloconNetworkCallDomainModel>

    suspend fun getCall(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        callId: String,
    ): FloconNetworkCallDomainModel?

    fun observeCall(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        callId: String,
    ): Flow<FloconNetworkCallDomainModel?>

    suspend fun save(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        call: FloconNetworkCallDomainModel,
    )

    suspend fun clearDeviceCalls(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel)

    suspend fun deleteRequest(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, callId: String)

    suspend fun deleteRequestsBefore(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, callId: String)

    suspend fun clear()

}
