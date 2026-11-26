package io.github.openflocon.domain.proxy

import io.github.openflocon.domain.models.settings.ServerPortsDomainModel
import io.github.openflocon.domain.models.settings.ServerPortsOverrides
import io.github.openflocon.domain.proxy.models.ProxyStateDomainModel
import io.github.openflocon.domain.proxy.repository.ProxyRepository
import io.github.openflocon.domain.proxy.usecase.RunProxyUseCase
import io.github.openflocon.domain.settings.FakeSettingsRepository
import io.github.openflocon.domain.settings.usecase.ObserveAppLanguageUseCase
import io.github.openflocon.domain.settings.usecase.ObserveServerPortsUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class RunProxyUseCaseTest {

    private class FakeProxyRepository : ProxyRepository {
        val calls = mutableListOf<String>()
        override val state: Flow<ProxyStateDomainModel> = MutableStateFlow(ProxyStateDomainModel.Stopped)
        override val isEnabled = MutableStateFlow(false)
        override suspend fun setEnabled(enabled: Boolean) {
            isEnabled.value = enabled
        }
        override suspend fun start(websocketPort: Int, languageTag: String?) {
            calls += "start $websocketPort"
        }
        override suspend fun stop() {
            calls += "stop"
        }
    }

    private val proxy = FakeProxyRepository()
    private val settings = FakeSettingsRepository().apply {
        savedPorts.value = ServerPortsDomainModel(websocketPort = 9023, httpPort = 9024)
    }
    private val useCase = RunProxyUseCase(proxy, ObserveServerPortsUseCase(settings, ServerPortsOverrides.None), ObserveAppLanguageUseCase(settings))

    @Test
    fun `follows the choice of the user and the websocket port`() = runTest {
        val job = launch { useCase() }
        runCurrent()
        assertEquals(listOf("stop"), proxy.calls)

        proxy.setEnabled(true)
        runCurrent()
        assertEquals(listOf("stop", "start 9023"), proxy.calls)

        // the http port is not used by the proxy: no restart
        settings.savedPorts.value = ServerPortsDomainModel(websocketPort = 9023, httpPort = 9500)
        runCurrent()
        assertEquals(listOf("stop", "start 9023"), proxy.calls)

        settings.savedPorts.value = ServerPortsDomainModel(websocketPort = 9100, httpPort = 9500)
        runCurrent()
        assertEquals(listOf("stop", "start 9023", "start 9100"), proxy.calls)

        proxy.setEnabled(false)
        runCurrent()
        assertEquals(listOf("stop", "start 9023", "start 9100", "stop"), proxy.calls)
        job.cancel()
    }

    @Test
    fun `starts at launch when it was left on`() = runTest {
        proxy.setEnabled(true)
        val job = launch { useCase() }
        runCurrent()
        assertEquals(listOf("start 9023"), proxy.calls)
        job.cancel()
    }
}
