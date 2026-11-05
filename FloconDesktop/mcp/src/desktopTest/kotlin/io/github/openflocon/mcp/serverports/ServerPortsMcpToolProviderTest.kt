package io.github.openflocon.mcp.serverports

import io.github.openflocon.domain.models.settings.AppScaleSetting
import io.github.openflocon.domain.models.settings.NetworkSettings
import io.github.openflocon.domain.models.settings.ServerPortsDomainModel
import io.github.openflocon.domain.models.settings.ServerPortsOverrides
import io.github.openflocon.domain.models.settings.ThemeSetting
import io.github.openflocon.domain.settings.repository.AdbForwardStatus
import io.github.openflocon.domain.settings.repository.SettingsRepository
import io.github.openflocon.domain.settings.usecase.ObserveServerPortsUseCase
import io.github.openflocon.domain.settings.usecase.UpdateServerPortsUseCase
import io.github.openflocon.mcp.McpTestServer
import io.github.openflocon.mcp.json
import io.github.openflocon.mcp.text
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ServerPortsMcpToolProviderTest {

    private var server: McpTestServer? = null

    private fun start(
        saved: ServerPortsDomainModel = ServerPortsDomainModel(),
        overrides: ServerPortsOverrides = ServerPortsOverrides.None,
    ): FakeSettingsRepository {
        val repository = FakeSettingsRepository(saved)
        server = McpTestServer(
            toolProviders = listOf(
                ServerPortsMcpToolProvider(
                    observeServerPorts = ObserveServerPortsUseCase(repository, overrides),
                    updateServerPorts = UpdateServerPortsUseCase(repository, overrides),
                    settingsRepository = repository,
                    overrides = overrides,
                ),
            ),
        )
        runBlocking { server!!.start() }
        return repository
    }

    @AfterTest
    fun tearDown() = runBlocking {
        server?.stop()
        Unit
    }

    private fun JsonObject.port(group: String, key: String) = this[group]!!.jsonObject[key]!!.jsonPrimitive.int

    @Test
    fun `only set_server_ports is not read only`() = runBlocking {
        start()
        val tools = server!!.listTools()

        assertEquals(setOf("get_server_ports", "set_server_ports"), tools.map { it.name }.toSet())
        assertEquals(listOf("set_server_ports"), tools.filter { it.annotations?.readOnlyHint != true }.map { it.name })
    }

    @Test
    fun `get_server_ports returns the defaults`() = runBlocking {
        start()
        val result = server!!.callTool("get_server_ports").json()

        assertEquals(9023, result.port("effective", "websocketPort"))
        assertEquals(9024, result.port("effective", "httpPort"))
        assertEquals(9023, result.port("defaults", "websocketPort"))
        assertEquals(9024, result.port("defaults", "httpPort"))
        assertEquals(false, result["websocketPortForcedByLaunchOption"]!!.jsonPrimitive.boolean)
        assertEquals(false, result["httpPortForcedByLaunchOption"]!!.jsonPrimitive.boolean)
    }

    @Test
    fun `get_server_ports returns effective, saved and overridden flags`() = runBlocking {
        start(
            saved = ServerPortsDomainModel(websocketPort = 9100, httpPort = 9101),
            overrides = ServerPortsOverrides(websocketPort = 9500),
        )
        val result = server!!.callTool("get_server_ports").json()

        assertEquals(9500, result.port("effective", "websocketPort"))
        assertEquals(9101, result.port("effective", "httpPort"))
        assertEquals(9100, result.port("saved", "websocketPort"))
        assertEquals(9101, result.port("saved", "httpPort"))
        assertEquals(true, result["websocketPortForcedByLaunchOption"]!!.jsonPrimitive.boolean)
        assertEquals(false, result["httpPortForcedByLaunchOption"]!!.jsonPrimitive.boolean)
    }

    @Test
    fun `set_server_ports saves valid ports`() = runBlocking {
        val repository = start()
        val result = server!!.callTool("set_server_ports", mapOf("websocketPort" to 9100, "httpPort" to 9101)).json()

        assertEquals(ServerPortsDomainModel(9100, 9101), repository.saved.value)
        assertEquals(9100, result.port("effective", "websocketPort"))
        assertEquals(9101, result.port("effective", "httpPort"))
        assertTrue(result["message"]!!.jsonPrimitive.content.contains("disconnected"))
    }

    @Test
    fun `set_server_ports with one port keeps the other one`() = runBlocking {
        val repository = start(saved = ServerPortsDomainModel(9100, 9101))
        server!!.callTool("set_server_ports", mapOf("httpPort" to 9200)).json()

        assertEquals(ServerPortsDomainModel(9100, 9200), repository.saved.value)
    }

    @Test
    fun `set_server_ports puts the defaults back`() = runBlocking {
        val repository = start(saved = ServerPortsDomainModel(9100, 9101))
        server!!.callTool("set_server_ports", mapOf("websocketPort" to 9023, "httpPort" to 9024)).json()

        assertEquals(ServerPortsDomainModel(), repository.saved.value)
    }

    @Test
    fun `set_server_ports rejects invalid ports and saves nothing`() = runBlocking {
        val repository = start()
        val invalid = listOf(
            mapOf("websocketPort" to 80) to "between",
            mapOf("httpPort" to 70000) to "between",
            mapOf("websocketPort" to 9100, "httpPort" to 9100) to "different",
            mapOf("websocketPort" to 9042) to "MCP",
            mapOf("httpPort" to 9042) to "MCP",
            mapOf("websocketPort" to 9024, "httpPort" to 9100) to "only be",
            mapOf("websocketPort" to 9100, "httpPort" to 9023) to "only be",
        )

        invalid.forEach { (arguments, reason) ->
            val result = server!!.callTool("set_server_ports", arguments)
            assertEquals(true, result.isError, "$arguments")
            assertTrue(result.text().contains(reason), "$arguments: ${result.text()}")
        }
        assertEquals(ServerPortsDomainModel(), repository.saved.value)
    }

    @Test
    fun `set_server_ports returns an error when a requested port is forced by a launch option`() = runBlocking {
        val repository = start(overrides = ServerPortsOverrides(httpPort = 9500))
        val result = server!!.callTool("set_server_ports", mapOf("httpPort" to 9200))

        assertEquals(true, result.isError)
        assertTrue(result.text().contains("launch option"), result.text())
        assertEquals(ServerPortsDomainModel(), repository.saved.value)
    }

    @Test
    fun `set_server_ports can change the port which is not forced`() = runBlocking {
        val repository = start(overrides = ServerPortsOverrides(httpPort = 9500))
        val result = server!!.callTool("set_server_ports", mapOf("websocketPort" to 9100)).json()

        assertEquals(ServerPortsDomainModel(9100, 9024), repository.saved.value)
        assertEquals(9100, result.port("effective", "websocketPort"))
        assertEquals(9500, result.port("effective", "httpPort"))
    }

    @Test
    fun `set_server_ports without argument returns an error`() = runBlocking {
        start()
        val result = server!!.callTool("set_server_ports")

        assertEquals(true, result.isError)
        assertTrue(result.text().contains("at least one"), result.text())
    }

    private class FakeSettingsRepository(initial: ServerPortsDomainModel) : SettingsRepository {
        val saved = MutableStateFlow(initial)
        override var networkSettings: NetworkSettings = NetworkSettings(
            pinnedDetails = false,
            displayOldSessions = false,
            autoScroll = false,
            invertList = false,
        )
        override val networkSettingsFlow: Flow<NetworkSettings> = emptyFlow()
        override fun getAdbPath(): String? = null
        override suspend fun setAdbPath(path: String) {}
        override suspend fun setAppScale(value: AppScaleSetting) {}
        override suspend fun setTheme(value: ThemeSetting) {}
        override fun getFloconDocumentsPath(): String? = null
        override suspend fun setFloconDocumentsPath(path: String) {}
        override val floconDocumentsPath: Flow<String?> = emptyFlow()
        override fun getDismissedDesktopVersion(): String? = null
        override suspend fun setDismissedDesktopVersion(version: String) {}
        override val dismissedClientVersionFlow: Flow<String?> = emptyFlow()
        override fun getDismissedClientVersion(): String? = null
        override suspend fun setDismissedClientVersion(version: String) {}
        override fun getLastSeenReleaseNotesVersion(): String? = null
        override suspend fun setLastSeenReleaseNotesVersion(version: String) {}
        override val adbPath: Flow<String?> = emptyFlow()
        override val savedServerPorts: Flow<ServerPortsDomainModel> = saved
        override suspend fun setServerPorts(ports: ServerPortsDomainModel) {
            saved.value = ports
        }
        override val appScale: StateFlow<AppScaleSetting> = MutableStateFlow(AppScaleSetting.DEFAULT)
        override val theme: StateFlow<ThemeSetting> = MutableStateFlow(ThemeSetting.DEFAULT)
        override val adbForwardStatus: StateFlow<AdbForwardStatus> = MutableStateFlow(AdbForwardStatus.UNKNOWN)
        override fun setAdbForwardStatus(status: AdbForwardStatus) {}
    }
}
