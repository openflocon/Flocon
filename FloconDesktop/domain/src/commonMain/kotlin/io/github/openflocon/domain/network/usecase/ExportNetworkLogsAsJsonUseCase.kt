package io.github.openflocon.domain.network.usecase

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.common.Success
import io.github.openflocon.domain.network.models.FloconNetworkCallDomainModel
import io.github.openflocon.domain.network.repository.NetworkRepository

class ExportNetworkLogsAsJsonUseCase(
    private val networkRepository: NetworkRepository,
) {
    suspend operator fun invoke(
        deviceId: String? = null,
        startTimestamp: Long? = null,
        endTimestamp: Long? = null,
    ): Either<Throwable, List<FloconNetworkCallDomainModel>> {
        return try {
            val calls = networkRepository.getNetworkCalls()
            
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