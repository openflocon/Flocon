package io.github.openflocon.domain.proxy.usecase

import io.github.openflocon.domain.proxy.repository.ProxyRepository
import io.github.openflocon.domain.settings.usecase.ObserveAppLanguageUseCase
import io.github.openflocon.domain.settings.usecase.ObserveServerPortsUseCase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * Keeps the proxy in line with the choice of the user, for as long as it is collected (the app lifetime):
 * started at launch when it was on, restarted when the websocket port or the language of Flocon (used by the
 * certificate page) changes, stopped when turned off.
 */
class RunProxyUseCase(
    private val proxyRepository: ProxyRepository,
    private val observeServerPortsUseCase: ObserveServerPortsUseCase,
    private val observeAppLanguageUseCase: ObserveAppLanguageUseCase,
) {
    suspend operator fun invoke() {
        combine(
            proxyRepository.isEnabled,
            observeServerPortsUseCase(),
            observeAppLanguageUseCase(),
        ) { enabled, ports, language -> if (enabled) ports.websocketPort to language.languageTag else null }
            .distinctUntilChanged()
            .collectLatest { config ->
                if (config == null) {
                    proxyRepository.stop()
                } else {
                    proxyRepository.start(websocketPort = config.first, languageTag = config.second)
                }
            }
    }
}
