package io.github.openflocon.domain.network.usecase

import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.network.repository.NetworkRepository
import kotlinx.coroutines.flow.firstOrNull

class ReplayNetworkCallUseCase(
    private val networkRepository: NetworkRepository,
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
) {
    suspend operator fun invoke(callId: String) {
        val deviceIdAndPackageName = getCurrentDeviceIdAndPackageNameUseCase() ?: return
        val call = networkRepository.observeRequest(
            deviceIdAndPackageName = deviceIdAndPackageName,
            requestId = callId
        ).firstOrNull() ?: return

        networkRepository.replayRequest(
            deviceIdAndPackageName = deviceIdAndPackageName,
            request = call
        )
    }
}
