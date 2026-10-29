package io.github.openflocon.mcp

import io.github.openflocon.domain.device.models.DeviceDomainModel
import io.github.openflocon.domain.network.usecase.GenerateCurlCommandUseCase
import io.github.openflocon.mcp.devices.DevicesMcpToolProvider
import io.github.openflocon.mcp.network.NetworkMcpToolProvider
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.sse.SSE
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.modelcontextprotocol.kotlin.sdk.client.Client
import io.modelcontextprotocol.kotlin.sdk.client.mcpStreamableHttp
import io.modelcontextprotocol.kotlin.sdk.types.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.types.TextContent
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.net.ServerSocket
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FloconMcpServerTest {

    private val pixel = DeviceDomainModel(deviceId = "pixel", deviceName = "Pixel 9", platform = "android")
    private val devicesRepository = FakeDevicesRepository(
        devicesWithApps = mapOf(pixel to listOf(app("io.flocon.sample", lastAppInstance = 2))),
        currentDeviceIdValue = "pixel",
        selectedApps = mapOf("pixel" to "io.flocon.sample"),
    )
    private val networkRepository = FakeNetworkRepository(
        calls = mapOf(
            ("pixel" to "io.flocon.sample") to listOf(
                httpCall("old-session", startTime = 1, appInstance = 1),
                httpCall("users", url = "https://api.flocon.io/users", startTime = 10, appInstance = 2),
                httpCall("login", url = "https://api.flocon.io/login", method = "POST", startTime = 20, appInstance = 2, httpCode = 401),
                httpCall("pending", url = "https://api.flocon.io/feed", startTime = 30, appInstance = 2, httpCode = null),
            ),
        ),
    )

    private val port = ServerSocket(0).use { it.localPort }
    private val server = FloconMcpServer(
        toolProviders = listOf(
            DevicesMcpToolProvider(devicesRepository),
            NetworkMcpToolProvider(
                networkRepository = networkRepository,
                targetResolver = targetResolver(devicesRepository),
                generateCurlCommandUseCase = GenerateCurlCommandUseCase(),
            ),
        ),
        port = port,
        logManager = NoOpLogManager,
    )
    private val httpClient = HttpClient(CIO) { install(SSE) }
    private lateinit var client: Client

    @BeforeTest
    fun setUp() = runBlocking {
        server.startServer()
        client = httpClient.mcpStreamableHttp("http://localhost:$port/mcp")
    }

    @AfterTest
    fun tearDown() = runBlocking {
        client.close()
        httpClient.close()
        server.stopServer()
    }

    @Test
    fun `exposes the read only tools`() = runBlocking {
        val tools = client.listTools().tools

        assertEquals(
            setOf("list_devices", "list_network_calls", "get_network_call", "search_network_calls"),
            tools.map { it.name }.toSet(),
        )
        assertTrue(tools.all { it.annotations?.readOnlyHint == true })
    }

    @Test
    fun `list_devices`() = runBlocking {
        val result = client.callTool("list_devices", emptyMap()).json()

        val device = result["devices"]!!.jsonArray.single().jsonObject
        assertEquals("pixel", device["deviceId"]!!.jsonPrimitive.content)
        assertEquals("default", device["connectionType"]!!.jsonPrimitive.content)
        assertEquals("io.flocon.sample", device["apps"]!!.jsonArray.single().jsonObject["packageName"]!!.jsonPrimitive.content)
    }

    @Test
    fun `list_network_calls returns the current session, newest first`() = runBlocking {
        val result = client.callTool("list_network_calls", emptyMap()).json()

        assertEquals(listOf("pending", "login", "users"), result.callIds())
        assertEquals("pixel", result["target"]!!.jsonObject["deviceId"]!!.jsonPrimitive.content)
    }

    @Test
    fun `list_network_calls filters`() = runBlocking {
        assertEquals(
            listOf("login"),
            client.callTool("list_network_calls", mapOf("onlyErrors" to true)).json().callIds(),
        )
        assertEquals(
            listOf("login"),
            client.callTool("list_network_calls", mapOf("methods" to listOf("post"))).json().callIds(),
        )
        assertEquals(
            listOf("users", "login"),
            client.callTool("list_network_calls", mapOf("order" to "oldest_first", "limit" to 2)).json().callIds(),
        )
        val withOldSessions = client.callTool("list_network_calls", mapOf("includeOldSessions" to true)).json()
        assertEquals(4, withOldSessions["totalMatching"]!!.jsonPrimitive.int)
    }

    @Test
    fun `get_network_call returns the details`() = runBlocking {
        val result = client.callTool("get_network_call", mapOf("callId" to "login")).json()

        assertEquals("POST", result["summary"]!!.jsonObject["method"]!!.jsonPrimitive.content)
        assertEquals(401, result["summary"]!!.jsonObject["httpCode"]!!.jsonPrimitive.int)
        assertEquals("Bearer token", result["request"]!!.jsonObject["headers"]!!.jsonObject["Authorization"]!!.jsonPrimitive.content)
        assertTrue(result["curl"]!!.jsonPrimitive.content.startsWith("curl -X POST"))
    }

    @Test
    fun `get_network_call with an unknown id is an error`() = runBlocking {
        val result = client.callTool("get_network_call", mapOf("callId" to "unknown"))

        assertEquals(true, result.isError)
        assertTrue(result.text().contains("not found"))
    }

    @Test
    fun `search_network_calls`() = runBlocking {
        val result = client.callTool("search_network_calls", mapOf("query" to "login", "scopes" to listOf("Url"))).json()

        assertEquals(listOf("login"), result.callIds())
    }

    @Test
    fun `requests from another host are rejected`() = runBlocking {
        val response = httpClient.post("http://localhost:$port/mcp") {
            header(HttpHeaders.Host, "evil.com")
            contentType(ContentType.Application.Json)
            setBody("""{"jsonrpc":"2.0","id":1,"method":"tools/list"}""")
        }

        assertEquals(HttpStatusCode.Forbidden, response.status)
    }

    private fun CallToolResult.text(): String = (content.single() as TextContent).text

    private fun CallToolResult.json(): JsonObject {
        assertEquals(false, isError, text())
        return Json.parseToJsonElement(text()).jsonObject
    }

    private fun JsonObject.callIds(): List<String> = this["calls"]!!.jsonArray.map { it.jsonObject["callId"]!!.jsonPrimitive.content }
}
