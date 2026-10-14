package io.github.openflocon.mcp

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.sse.SSE
import io.modelcontextprotocol.kotlin.sdk.client.Client
import io.modelcontextprotocol.kotlin.sdk.client.mcpStreamableHttp
import io.modelcontextprotocol.kotlin.sdk.types.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.types.TextContent
import io.modelcontextprotocol.kotlin.sdk.types.Tool
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import java.net.ServerSocket
import kotlin.test.assertEquals

/**
 * Starts a [FloconMcpServer] with the given tools, and an MCP client connected to it
 */
internal class McpTestServer(toolProviders: List<McpToolProvider>) {

    private val port = ServerSocket(0).use { it.localPort }
    private val server = FloconMcpServer(toolProviders = toolProviders, port = port)
    private val httpClient = HttpClient(CIO) { install(SSE) }
    private lateinit var client: Client

    suspend fun start() {
        server.startServer()
        client = httpClient.mcpStreamableHttp("http://localhost:$port/mcp")
    }

    suspend fun stop() {
        client.close()
        httpClient.close()
        server.stopServer()
    }

    suspend fun listTools(): List<Tool> = client.listTools().tools

    suspend fun callTool(name: String, arguments: Map<String, Any?> = emptyMap()): CallToolResult = client.callTool(name, arguments)
}

internal fun CallToolResult.text(): String = (content.single() as TextContent).text

internal fun CallToolResult.json(): JsonObject {
    assertEquals(false, isError, text())
    return Json.parseToJsonElement(text()).jsonObject
}
