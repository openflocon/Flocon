package io.github.openflocon.domain.proxy.usecase

import io.github.openflocon.domain.proxy.repository.ProxyRepository
import io.github.openflocon.domain.settings.usecase.ObserveServerPortsUseCase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * Keeps the proxy in line with the choice of the user, for as long as it is collected (the app lifetime):
 * started at launch when it was on, restarted when the websocket port changes, stopped when turned off.
 */
class RunProxyUseCase(
    private val proxyRepository: ProxyRepository,
    private val observeServerPortsUseCase: ObserveServerPortsUseCase,
) {
    suspend operator fun invoke() {
        combine(
            proxyRepository.isEnabled,
            observeServerPortsUseCase(),
        ) { enabled, ports -> ports.websocketPort.takeIf { enabled } }
            .distinctUntilChanged()
            .collectLatest { websocketPort ->
                if (websocketPort == null) {
                    proxyRepository.stop()
                } else {
                    proxyRepository.start(websocketPort)
                }
            }
    }
}
