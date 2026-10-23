package io.github.openflocon.domain.settings.usecase

import io.github.openflocon.domain.settings.repository.NetworkAddressRepository

class GetLocalNetworkAddressesUseCase(
    private val networkAddressRepository: NetworkAddressRepository,
) {

    operator fun invoke(): List<String> = networkAddressRepository.getLocalAddresses()
}
