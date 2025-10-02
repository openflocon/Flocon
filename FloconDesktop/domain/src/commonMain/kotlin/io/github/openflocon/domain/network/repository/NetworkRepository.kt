package io.github.openflocon.domain.network.repository

import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.network.models.NetworkFilterDomainModel
import io.github.openflocon.domain.network.models.NetworkSortDomainModel
import kotlinx.coroutines.flow.Flow

interface NetworkRepository {

    fun observeRequests(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        sortedBy: NetworkSortDomainModel?,
        filter: NetworkFilterDomainModel,
    ): Flow<List<FloconNetworkCallDomainModel>>

    fun observeRequest(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        requestId: String,
    ): Flow<FloconNetworkCallDomainModel?>

    suspend fun clear()
    suspend fun clearDeviceCalls(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel)

    suspend fun deleteRequest(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        requestId: String,
    )

    suspend fun deleteRequestsBefore(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        requestId: String,
    )

    suspend fun getRequests(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        ids: List<String>
    ) : List<FloconNetworkCallDomainModel>

    suspend fun deleteOldRequests(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel
    )
}
