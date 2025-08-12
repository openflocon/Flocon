package io.github.openflocon.domain.network.usecase

import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdUseCase
import io.github.openflocon.domain.network.repository.NetworkRepository

class RemoveHttpRequestUseCase(
    private val networkRepository: NetworkRepository,
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
) {
    suspend operator fun invoke(requestId: String) {
        getCurrentDeviceIdAndPackageNameUseCase()?.let { deviceIdAndPackageName ->
            networkRepository.deleteRequest(deviceIdAndPackageName = deviceIdAndPackageName, requestId = requestId)
        }
    }
}
