package io.github.openflocon.domain.settings

import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.common.Success
import io.github.openflocon.domain.models.settings.ServerPortsDomainModel
import io.github.openflocon.domain.models.settings.ServerPortsOverrides
import io.github.openflocon.domain.settings.usecase.ObserveServerPortsUseCase
import io.github.openflocon.domain.settings.usecase.ServerPortsError
import io.github.openflocon.domain.settings.usecase.UpdateServerPortsUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UpdateServerPortsUseCaseTest {

    private val repository = FakeSettingsRepository()
    private val saved = repository.savedPorts
    private val update = UpdateServerPortsUseCase(repository, ServerPortsOverrides.None)

    @Test
    fun `valid ports are saved and observed`() = runBlocking {
        val result = update(ServerPortsDomainModel(websocketPort = 9100, httpPort = 9101))

        assertTrue(result is Success)
        assertEquals(
            ServerPortsDomainModel(websocketPort = 9100, httpPort = 9101),
            ObserveServerPortsUseCase(repository, ServerPortsOverrides.None)().first(),
        )
    }

    @Test
    fun `the bounds of the range are accepted`() = runBlocking {
        assertTrue(update(ServerPortsDomainModel(websocketPort = 1024, httpPort = 65535)) is Success)
    }

    @Test
    fun `ports out of range are refused and not saved`() = runBlocking {
        listOf(
            ServerPortsDomainModel(websocketPort = 1023, httpPort = 9024),
            ServerPortsDomainModel(websocketPort = 9023, httpPort = 65536),
            ServerPortsDomainModel(websocketPort = 0, httpPort = 9024),
            ServerPortsDomainModel(websocketPort = -1, httpPort = 9024),
        ).forEach { ports ->
            assertEquals(Failure(ServerPortsError.OutOfRange), update(ports))
        }
        assertEquals(ServerPortsDomainModel(), saved.value)
    }

    @Test
    fun `the same port for both servers is refused`() = runBlocking {
        assertEquals(
            Failure(ServerPortsError.SamePorts),
            update(ServerPortsDomainModel(websocketPort = 9100, httpPort = 9100)),
        )
        assertEquals(ServerPortsDomainModel(), saved.value)
    }

    @Test
    fun `the mcp port is refused for either server`() = runBlocking {
        assertEquals(
            Failure(ServerPortsError.McpPortReserved),
            update(ServerPortsDomainModel(websocketPort = 9042, httpPort = 9024)),
        )
        assertEquals(
            Failure(ServerPortsError.McpPortReserved),
            update(ServerPortsDomainModel(websocketPort = 9023, httpPort = 9042)),
        )
        assertEquals(ServerPortsDomainModel(), saved.value)
    }

    @Test
    fun `a default port used in the other slot is refused`() = runBlocking {
        listOf(
            ServerPortsDomainModel(websocketPort = 9024, httpPort = 9025),
            ServerPortsDomainModel(websocketPort = 9100, httpPort = 9023),
        ).forEach { ports ->
            assertEquals(Failure(ServerPortsError.ReservedDefaultPort), update(ports))
        }
        assertEquals(ServerPortsDomainModel(), saved.value)
    }

    @Test
    fun `default ports in their own slot are accepted`() = runBlocking {
        assertTrue(update(ServerPortsDomainModel(websocketPort = 9023, httpPort = 9024)) is Success)
        assertTrue(update(ServerPortsDomainModel(websocketPort = 9100, httpPort = 9101)) is Success)
    }

    @Test
    fun `an overridden port is never persisted`() = runBlocking {
        saved.value = ServerPortsDomainModel(websocketPort = 9300, httpPort = 9301)
        val overridden = UpdateServerPortsUseCase(repository, ServerPortsOverrides(websocketPort = 9100))

        assertTrue(overridden(ServerPortsDomainModel(websocketPort = 9100, httpPort = 9401)) is Success)

        // the websocket port stays the saved one, the not overridden http port is saved
        assertEquals(ServerPortsDomainModel(9300, 9401), saved.value)
        assertEquals(
            ServerPortsDomainModel(9100, 9401),
            ObserveServerPortsUseCase(repository, ServerPortsOverrides(websocketPort = 9100))().first(),
        )
    }

    @Test
    fun `nothing is persisted when both ports are overridden`() = runBlocking {
        saved.value = ServerPortsDomainModel(websocketPort = 9300, httpPort = 9301)
        val overridden = UpdateServerPortsUseCase(repository, ServerPortsOverrides(websocketPort = 9100, httpPort = 9101))

        assertTrue(overridden(ServerPortsDomainModel(9100, 9101)) is Success)

        assertEquals(ServerPortsDomainModel(9300, 9301), saved.value)
    }
}
