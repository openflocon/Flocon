package io.github.openflocon.domain.network.usecase

import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.network.repository.NetworkRepository

class ResetCurrentDeviceHttpRequestsUseCase(
    private val networkRepository: NetworkRepository,
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
) {
    suspend operator fun invoke() {
        getCurrentDeviceIdAndPackageNameUseCase()?.let {
            networkRepository.clearDeviceCalls(deviceIdAndPackageName = it)
        }
    }
}
