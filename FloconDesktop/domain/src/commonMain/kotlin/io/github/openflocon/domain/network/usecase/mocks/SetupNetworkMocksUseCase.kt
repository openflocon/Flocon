package io.github.openflocon.domain.network.usecase.mocks

import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.network.repository.NetworkMocksRepository

class SetupNetworkMocksUseCase(
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val networkMocksRepository: NetworkMocksRepository,
) {
    suspend operator fun invoke() {
        getCurrentDeviceIdAndPackageNameUseCase()?.let { deviceIdAndPackageName ->
            networkMocksRepository.setupMocks(
                mocks = networkMocksRepository.getAllEnabledMocks(deviceIdAndPackageName),
                deviceIdAndPackageName = deviceIdAndPackageName,
            )
        }
    }
}
