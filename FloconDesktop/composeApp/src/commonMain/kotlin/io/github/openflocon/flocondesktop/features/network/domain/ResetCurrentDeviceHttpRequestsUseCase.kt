package io.github.openflocon.flocondesktop.features.network.domain

import io.github.openflocon.flocondesktop.core.domain.device.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.flocondesktop.features.network.domain.repository.NetworkRepository

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
