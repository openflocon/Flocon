package io.github.openflocon.domain.network.usecase.mocks

import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.network.models.MockNetworkDomainModel
import io.github.openflocon.domain.network.repository.NetworkMocksRepository

class GetNetworkMockByIdUseCase(
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val networkMocksRepository: NetworkMocksRepository,
) {
    suspend operator fun invoke(
        id: String
    ): MockNetworkDomainModel? {
        return getCurrentDeviceIdAndPackageNameUseCase()?.let { deviceIdAndPackageName ->
            networkMocksRepository.getMock(
                deviceIdAndPackageName = deviceIdAndPackageName,
                id = id,
            )
        }
    }
}
