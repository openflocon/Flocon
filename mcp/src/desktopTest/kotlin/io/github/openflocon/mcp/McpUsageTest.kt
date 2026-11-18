package io.github.openflocon.mcp

import io.github.openflocon.domain.device.models.DeviceDomainModel
import io.github.openflocon.mcp.devices.DevicesMcpToolProvider
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.runBlocking
import java.net.ServerSocket
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class McpUsageTest {

    @Test
    fun `client names are restricted to the allow list`() {
        assertEquals("claude", McpClients.category("claude-code"))
        assertEquals("claude", McpClients.category("Claude Desktop"))
        assertEquals("cursor", McpClients.category("Cursor"))
        assertEquals("codex", McpClients.category("codex-mcp-client"))
        assertEquals("windsurf", McpClients.category("windsurf"))
        assertEquals("vscode", McpClients.category("Visual Studio Code"))
        assertEquals("zed", McpClients.category("zed"))
        assertEquals("other", McpClients.category("my-secret-internal-agent"))
        assertEquals("unknown", McpClients.category(null))
        assertEquals("unknown", McpClients.category("  "))
        listOf("claude-code", "x", "ZED", "").forEach { assertTrue(McpClients.category(it) in McpClients.ALLOWED) }
    }

    @Test
    fun `the client name is read from the initialize request only`() {
        val initialize = """{"jsonrpc":"2.0","id":1,"method":"initialize","params":{"protocolVersion":"2025-03-26","capabilities":{},"clientInfo":{"name":"Cursor","version":"1"}}}"""
        assertEquals("Cursor", initializeClientName(initialize))
        assertEquals("Cursor", initializeClientName("[$initialize]"))
        assertNull(initializeClientName("""{"jsonrpc":"2.0","id":1,"method":"tools/call","params":{"name":"list_devices"}}"""))
        assertNull(initializeClientName("not json"))
    }

    @Test
    fun `a tool call reports its name and the client category, nothing else`() = runBlocking {
        val calls = mutableListOf<Pair<String, String>>()
        val devices = FakeDevicesRepository(
            devicesWithApps = mapOf(DeviceDomainModel("pixel", "Pixel 9", "android") to emptyList()),
            currentDeviceIdValue = "pixel",
            selectedApps = emptyMap(),
        )
        val port = ServerSocket(0).use { it.localPort }
        val server = FloconMcpServer(
            toolProviders = listOf(DevicesMcpToolProvider(devices)),
            port = port,
            logManager = NoOpLogManager,
            usageTracker = { tool, client -> calls += tool to client },
        )
        val http = HttpClient(CIO)
        server.startServer()
        try {
            suspend fun post(body: String) = http.post("http://localhost:$port/mcp") {
                contentType(ContentType.Application.Json)
                header(HttpHeaders.Accept, "application/json, text/event-stream")
                setBody(body)
            }

            val initialize = post(
                """{"jsonrpc":"2.0","id":1,"method":"initialize","params":{"protocolVersion":"2025-03-26","capabilities":{},"clientInfo":{"name":"Cursor-secret-build","version":"1"}}}""",
            )
            assertEquals(HttpStatusCode.OK, initialize.status)
            val call = post("""{"jsonrpc":"2.0","id":2,"method":"tools/call","params":{"name":"list_devices","arguments":{}}}""")
            assertEquals(HttpStatusCode.OK, call.status)

            assertEquals(listOf("list_devices" to "cursor"), calls)
        } finally {
            http.close()
            server.stopServer()
        }
    }
}
