package io.github.openflocon.domain.proxy.usecase

import io.github.openflocon.domain.proxy.models.ProxyStateDomainModel
import io.github.openflocon.domain.proxy.repository.ProxyRepository
import kotlinx.coroutines.flow.Flow

class ObserveProxyStateUseCase(
    private val proxyRepository: ProxyRepository,
) {
    operator fun invoke(): Flow<ProxyStateDomainModel> = proxyRepository.state
}
