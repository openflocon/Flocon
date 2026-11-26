package io.github.openflocon.flocondesktop.features.proxy

import io.github.openflocon.domain.proxy.models.ProxyStateDomainModel
import io.github.openflocon.flocondesktop.features.proxy.mapper.mapToProxyUiState
import io.github.openflocon.flocondesktop.features.proxy.model.ProxyAddressUiModel
import io.github.openflocon.flocondesktop.features.proxy.model.ProxyMoodUiModel
import io.github.openflocon.flocondesktop.features.proxy.model.ProxyStatusUiModel
import kotlin.test.Test
import kotlin.test.assertEquals

class ProxyMapperTest {

    @Test
    fun `off whatever the process state`() {
        assertEquals(ProxyStatusUiModel.Off, mapToProxyUiState(false, ProxyStateDomainModel.Running(8080, 8081, listOf("1.2.3.4")), mascotEnabled = true).status)
    }

    @Test
    fun `turned on but not started yet shows starting`() {
        assertEquals(ProxyStatusUiModel.Starting, mapToProxyUiState(true, ProxyStateDomainModel.Stopped, mascotEnabled = true).status)
        assertEquals(ProxyStatusUiModel.Starting, mapToProxyUiState(true, ProxyStateDomainModel.Starting, mascotEnabled = true).status)
    }

    @Test
    fun `running gives one address per reachable ip with its interface and ca page`() {
        val status = mapToProxyUiState(
            enabled = true,
            state = ProxyStateDomainModel.Running(8080, 8081, listOf("192.168.1.12", "10.0.0.2", "169.254.10.1")),
            mascotEnabled = true,
            interfaceNames = mapOf("192.168.1.12" to "en0"),
        ).status
        assertEquals(
            listOf(
                ProxyAddressUiModel("192.168.1.12", "en0", "8080", "http://192.168.1.12:8081/"),
                ProxyAddressUiModel("10.0.0.2", null, "8080", "http://10.0.0.2:8081/"),
            ),
            (status as ProxyStatusUiModel.Running).addresses,
        )
    }

    @Test
    fun `the mood follows the status`() {
        assertEquals(ProxyMoodUiModel.Asleep, mapToProxyUiState(false, ProxyStateDomainModel.Stopped, mascotEnabled = true).mood)
        assertEquals(ProxyMoodUiModel.Asleep, mapToProxyUiState(true, ProxyStateDomainModel.Starting, mascotEnabled = true).mood)
        assertEquals(ProxyMoodUiModel.Watching, mapToProxyUiState(true, ProxyStateDomainModel.Running(8080, 8081, listOf("1.2.3.4")), mascotEnabled = true).mood)
        assertEquals(ProxyMoodUiModel.Glitched, mapToProxyUiState(true, ProxyStateDomainModel.Failed("boom"), mascotEnabled = true).mood)
        assertEquals(ProxyMoodUiModel.Asleep, mapToProxyUiState(true, ProxyStateDomainModel.Downloading(0.5f), mascotEnabled = true).mood)
    }

    @Test
    fun `failures are kept apart`() {
        assertEquals(ProxyStatusUiModel.Downloading(42), mapToProxyUiState(true, ProxyStateDomainModel.Downloading(0.42f), mascotEnabled = true).status)
        assertEquals(ProxyStatusUiModel.Downloading(null), mapToProxyUiState(true, ProxyStateDomainModel.Downloading(null), mascotEnabled = true).status)
        assertEquals(ProxyStatusUiModel.Failed("boom"), mapToProxyUiState(true, ProxyStateDomainModel.Failed("boom"), mascotEnabled = true).status)
    }
}
