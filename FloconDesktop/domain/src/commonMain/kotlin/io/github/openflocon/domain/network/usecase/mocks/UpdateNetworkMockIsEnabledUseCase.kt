package io.github.openflocon.domain.network.usecase.mocks

import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.network.repository.NetworkMocksRepository

class UpdateNetworkMockIsEnabledUseCase(
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val networkMocksRepository: NetworkMocksRepository,
    private val setupNetworkMocksUseCase: SetupNetworkMocksUseCase,
) {
    suspend operator fun invoke(
        id: String,
        isEnabled: Boolean,
    ) {
        getCurrentDeviceIdAndPackageNameUseCase()?.let { deviceIdAndPackageName ->
            networkMocksRepository.updateMockIsEnabled(
                deviceIdAndPackageName = deviceIdAndPackageName,
                id = id,
                isEnabled = isEnabled,
            )
            // after a change, update the device mocks
            setupNetworkMocksUseCase()
        }
    }
}
