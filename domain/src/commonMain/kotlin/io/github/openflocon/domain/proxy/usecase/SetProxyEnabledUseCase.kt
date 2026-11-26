package io.github.openflocon.domain.proxy.usecase

import io.github.openflocon.domain.proxy.repository.ProxyRepository

/** Saves the choice: [RunProxyUseCase] starts or stops the proxy from it. */
class SetProxyEnabledUseCase(
    private val proxyRepository: ProxyRepository,
) {
    suspend operator fun invoke(enabled: Boolean) {
        proxyRepository.setEnabled(enabled)
    }
}
