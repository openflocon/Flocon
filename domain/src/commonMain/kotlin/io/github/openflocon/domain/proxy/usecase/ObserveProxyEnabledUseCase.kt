package io.github.openflocon.domain.proxy.usecase

import io.github.openflocon.domain.proxy.repository.ProxyRepository
import kotlinx.coroutines.flow.Flow

class ObserveProxyEnabledUseCase(
    private val proxyRepository: ProxyRepository,
) {
    operator fun invoke(): Flow<Boolean> = proxyRepository.isEnabled
}
