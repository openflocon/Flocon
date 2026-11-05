package io.github.openflocon.domain.settings

import io.github.openflocon.domain.models.settings.ServerPortsDomainModel
import io.github.openflocon.domain.models.settings.ServerPortsOverrides
import io.github.openflocon.domain.settings.usecase.ObserveServerPortsUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class ObserveServerPortsUseCaseTest {

    private val repository = FakeSettingsRepository().apply {
        savedPorts.value = ServerPortsDomainModel(websocketPort = 9300, httpPort = 9301)
    }

    private fun observe(overrides: ServerPortsOverrides) = ObserveServerPortsUseCase(repository, overrides)

    @Test
    fun `without override the saved ports are emitted`() = runBlocking {
        assertEquals(ServerPortsDomainModel(9300, 9301), observe(ServerPortsOverrides.None)().first())
    }

    @Test
    fun `an override is merged with the saved ports`() = runBlocking {
        assertEquals(
            ServerPortsDomainModel(9100, 9301),
            observe(ServerPortsOverrides(websocketPort = 9100))().first(),
        )
    }

    @Test
    fun `both ports overridden`() = runBlocking {
        assertEquals(
            ServerPortsDomainModel(9100, 9101),
            observe(ServerPortsOverrides(websocketPort = 9100, httpPort = 9101))().first(),
        )
    }
}
