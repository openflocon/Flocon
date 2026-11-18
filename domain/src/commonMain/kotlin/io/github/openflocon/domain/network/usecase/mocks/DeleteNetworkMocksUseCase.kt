package io.github.openflocon.domain.network.usecase.mocks

import io.github.openflocon.domain.network.repository.NetworkMocksRepository

class DeleteNetworkMocksUseCase(
    private val networkMocksRepository: NetworkMocksRepository,
    private val setupNetworkMocksUseCase: SetupNetworkMocksUseCase,
) {
    suspend operator fun invoke(
        id: String
    ) {
        networkMocksRepository.deleteMock(
            id = id,
        )
        // after a change, update the device mocks
        setupNetworkMocksUseCase()
    }
}
