package io.github.openflocon.flocondesktop.features.proxy.mapper

import io.github.openflocon.domain.proxy.models.ProxyStateDomainModel
import io.github.openflocon.flocondesktop.features.proxy.model.ProxyAddressUiModel
import io.github.openflocon.flocondesktop.features.proxy.model.ProxyMoodUiModel
import io.github.openflocon.flocondesktop.features.proxy.model.ProxyStatusUiModel
import io.github.openflocon.flocondesktop.features.proxy.model.ProxyUiState
import kotlinx.collections.immutable.toImmutableList

internal fun mapToProxyUiState(
    enabled: Boolean,
    state: ProxyStateDomainModel,
    mascotEnabled: Boolean,
    interfaceNames: Map<String, String> = emptyMap(),
): ProxyUiState {
    val status = proxyStatus(enabled, state, interfaceNames)
    return ProxyUiState(
        enabled = enabled,
        status = status,
        mood = when (status) {
            is ProxyStatusUiModel.Running -> ProxyMoodUiModel.Watching
            is ProxyStatusUiModel.Failed -> ProxyMoodUiModel.Glitched
            ProxyStatusUiModel.Off, ProxyStatusUiModel.Starting, is ProxyStatusUiModel.Downloading -> ProxyMoodUiModel.Asleep
        },
        mascotEnabled = mascotEnabled,
    )
}

private fun proxyStatus(
    enabled: Boolean,
    state: ProxyStateDomainModel,
    interfaceNames: Map<String, String>,
): ProxyStatusUiModel = if (!enabled) {
    ProxyStatusUiModel.Off
} else {
    when (state) {
        // turned on, the process is not started yet
        ProxyStateDomainModel.Stopped,
        ProxyStateDomainModel.Starting,
        -> ProxyStatusUiModel.Starting

        is ProxyStateDomainModel.Downloading -> ProxyStatusUiModel.Downloading(state.progress?.let { (it * 100).toInt() })

        is ProxyStateDomainModel.Failed -> ProxyStatusUiModel.Failed(state.message)

        is ProxyStateDomainModel.Running -> ProxyStatusUiModel.Running(
            addresses = state.ips
                // link-local addresses (169.254.x.x, a USB or Thunderbolt link) are not reachable from a phone on Wi-Fi
                .filterNot { it.startsWith(LINK_LOCAL_PREFIX) }
                .map { ip ->
                    // the CA page is plain http: the phone does not trust the Flocon CA yet
                    ProxyAddressUiModel(ip = ip, interfaceName = interfaceNames[ip], proxyPort = state.proxyPort.toString(), caUrl = "http://$ip:${state.caPort}/")
                }
                .toImmutableList(),
        )
    }
}

private const val LINK_LOCAL_PREFIX = "169.254."
