package io.github.openflocon.domain.network.usecase.mocks

import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.network.repository.NetworkMocksRepository

class DeleteNetworkMocksUseCase(
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val networkMocksRepository: NetworkMocksRepository,
    private val setupNetworkMocksUseCase: SetupNetworkMocksUseCase,
) {
    suspend operator fun invoke(
        id: String
    ) {
        getCurrentDeviceIdAndPackageNameUseCase()?.let { deviceIdAndPackageName ->
            networkMocksRepository.deleteMock(
                deviceIdAndPackageName = deviceIdAndPackageName,
                id = id,
            )
            // after a change, update the device mocks
            setupNetworkMocksUseCase()
        }
    }
}
