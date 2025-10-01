package io.github.openflocon.domain.network.usecase.mocks

import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.network.repository.NetworkMocksRepository

class UpdateNetworkMockIsEnabledUseCase(
    private val networkMocksRepository: NetworkMocksRepository,
    private val setupNetworkMocksUseCase: SetupNetworkMocksUseCase,
) {
    suspend operator fun invoke(
        id: String,
        isEnabled: Boolean,
    ) {
        networkMocksRepository.updateMockIsEnabled(
            id = id,
            isEnabled = isEnabled,
        )
        // after a change, update the device mocks
        setupNetworkMocksUseCase()
    }
}
