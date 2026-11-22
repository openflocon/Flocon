package io.github.openflocon.flocondesktop.features.proxy

import io.github.openflocon.domain.proxy.models.ProxyStateDomainModel
import io.github.openflocon.flocondesktop.features.proxy.mapper.mapToProxyUiState
import io.github.openflocon.flocondesktop.features.proxy.model.ProxyAddressUiModel
import io.github.openflocon.flocondesktop.features.proxy.model.ProxyStatusUiModel
import kotlin.test.Test
import kotlin.test.assertEquals

class ProxyMapperTest {

    @Test
    fun `off whatever the process state`() {
        assertEquals(ProxyStatusUiModel.Off, mapToProxyUiState(false, ProxyStateDomainModel.Running(8080, 8081, listOf("1.2.3.4"))).status)
    }

    @Test
    fun `turned on but not started yet shows starting`() {
        assertEquals(ProxyStatusUiModel.Starting, mapToProxyUiState(true, ProxyStateDomainModel.Stopped).status)
        assertEquals(ProxyStatusUiModel.Starting, mapToProxyUiState(true, ProxyStateDomainModel.Starting).status)
    }

    @Test
    fun `running gives one address per ip with its ca page`() {
        val status = mapToProxyUiState(true, ProxyStateDomainModel.Running(8080, 8081, listOf("192.168.1.12", "10.0.0.2"))).status
        assertEquals(
            listOf(
                ProxyAddressUiModel("192.168.1.12", "8080", "http://192.168.1.12:8081/"),
                ProxyAddressUiModel("10.0.0.2", "8080", "http://10.0.0.2:8081/"),
            ),
            (status as ProxyStatusUiModel.Running).addresses,
        )
    }

    @Test
    fun `failures are kept apart`() {
        assertEquals(ProxyStatusUiModel.BinaryNotFound, mapToProxyUiState(true, ProxyStateDomainModel.BinaryNotFound).status)
        assertEquals(ProxyStatusUiModel.Failed("boom"), mapToProxyUiState(true, ProxyStateDomainModel.Failed("boom")).status)
    }
}
