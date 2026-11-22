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
): ProxyUiState {
    val status = proxyStatus(enabled, state)
    return ProxyUiState(
        enabled = enabled,
        status = status,
        mood = when (status) {
            is ProxyStatusUiModel.Running -> ProxyMoodUiModel.Watching
            ProxyStatusUiModel.BinaryNotFound, is ProxyStatusUiModel.Failed -> ProxyMoodUiModel.Glitched
            ProxyStatusUiModel.Off, ProxyStatusUiModel.Starting -> ProxyMoodUiModel.Asleep
        },
        mascotEnabled = mascotEnabled,
    )
}

private fun proxyStatus(enabled: Boolean, state: ProxyStateDomainModel): ProxyStatusUiModel = if (!enabled) {
    ProxyStatusUiModel.Off
} else {
    when (state) {
        // turned on, the process is not started yet
        ProxyStateDomainModel.Stopped,
        ProxyStateDomainModel.Starting,
        -> ProxyStatusUiModel.Starting

        ProxyStateDomainModel.BinaryNotFound -> ProxyStatusUiModel.BinaryNotFound

        is ProxyStateDomainModel.Failed -> ProxyStatusUiModel.Failed(state.message)

        is ProxyStateDomainModel.Running -> ProxyStatusUiModel.Running(
            addresses = state.ips
                .map { ip ->
                    // the CA page is plain http: the phone does not trust the Flocon CA yet
                    ProxyAddressUiModel(ip = ip, proxyPort = state.proxyPort.toString(), caUrl = "http://$ip:${state.caPort}/")
                }
                .toImmutableList(),
        )
    }
}
