package io.github.openflocon.domain.network.usecase.mocks

import io.github.openflocon.domain.network.models.MockNetworkDomainModel
import io.github.openflocon.domain.network.repository.NetworkMocksRepository

class GetNetworkMockByIdUseCase(
    private val networkMocksRepository: NetworkMocksRepository,
) {
    suspend operator fun invoke(
        id: String
    ): MockNetworkDomainModel? = networkMocksRepository.getMock(
        id = id,
    )
}
