package io.github.openflocon.domain.network.usecase

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.common.Success
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.domain.network.models.NetworkFilterDomainModel
import io.github.openflocon.domain.network.repository.NetworkRepository

class ExportNetworkLogsAsJsonUseCase(
    private val networkRepository: NetworkRepository,
) {
    suspend operator fun invoke(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        deviceId: String? = null,
        startTimestamp: Long? = null,
        endTimestamp: Long? = null,
    ): Either<Throwable, List<FloconNetworkCallDomainModel>> {
        return try {
            val filter = NetworkFilterDomainModel(
                statusCode = null,
                contentType = null,
                hasResponse = null,
                searchText = null,
                requestDate = null,
            )
            val calls = networkRepository.getRequests(
                deviceIdAndPackageName = deviceIdAndPackageName,
                sortedBy = null,
                filter = filter,
            )
            
            val filtered = calls.filter { call ->
                val matchesDevice = deviceId == null || call.appInstance.deviceId == deviceId
                val matchesStartTime = startTimestamp == null || call.request.startTime >= startTimestamp
                val matchesEndTime = endTimestamp == null || call.request.startTime <= endTimestamp
                
                matchesDevice && matchesStartTime && matchesEndTime
            }
            
            if (filtered.isEmpty()) {
                return Failure(Throwable("No network logs found matching the criteria"))
            }
            
            Success(filtered)
        } catch (e: Exception) {
            Failure(e)
        }
    }
}
