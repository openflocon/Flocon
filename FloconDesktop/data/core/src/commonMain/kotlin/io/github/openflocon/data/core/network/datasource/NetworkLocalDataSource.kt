package io.github.openflocon.data.core.network.datasource

import androidx.paging.PagingData
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.domain.network.models.NetworkFilterDomainModel
import io.github.openflocon.domain.network.models.NetworkSortDomainModel
import kotlinx.coroutines.flow.Flow

interface NetworkLocalDataSource {

    suspend fun getRequests(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        sortedBy: NetworkSortDomainModel?,
        filter: NetworkFilterDomainModel,
    ): List<FloconNetworkCallDomainModel>

    fun observeRequests(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        sortedBy: NetworkSortDomainModel?,
        filter: NetworkFilterDomainModel,
    ): Flow<PagingData<FloconNetworkCallDomainModel>>

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

    suspend fun save(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        calls: List<FloconNetworkCallDomainModel>,
    )

    suspend fun clearDeviceCalls(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel)

    suspend fun deleteRequest(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, callId: String)

    suspend fun deleteRequestsBefore(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, callId: String)

    suspend fun deleteRequestOnDifferentSession(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel)

    suspend fun clear()
}
