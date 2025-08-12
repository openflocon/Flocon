package io.github.openflocon.domain.network.usecase.mocks

import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdUseCase
import io.github.openflocon.domain.network.models.MockNetworkResponseDomainModel
import io.github.openflocon.domain.network.repository.NetworkMocksRepository

class AddNetworkMocksUseCase(
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val networkMocksRepository: NetworkMocksRepository,
    private val setupNetworkMocksUseCase: SetupNetworkMocksUseCase,
) {
    suspend operator fun invoke(
        mock: MockNetworkResponseDomainModel,
    ) {
        getCurrentDeviceIdAndPackageNameUseCase()?.let { deviceIdAndPackageName ->
            networkMocksRepository.addMock(
                deviceIdAndPackageName = deviceIdAndPackageName,
                mock = mock,
            )
            // after a change, update the device mocks
            setupNetworkMocksUseCase()
        }
    }
}
