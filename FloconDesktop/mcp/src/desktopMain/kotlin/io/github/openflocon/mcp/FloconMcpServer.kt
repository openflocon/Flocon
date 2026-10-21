package io.github.openflocon.mcp

import co.touchlab.kermit.Logger
import io.github.openflocon.domain.Constant
import io.github.openflocon.domain.mcp.repository.McpServerRepository
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.server.ServerOptions
import io.modelcontextprotocol.kotlin.sdk.server.mcpStatelessStreamableHttp
import io.modelcontextprotocol.kotlin.sdk.types.Implementation
import io.modelcontextprotocol.kotlin.sdk.types.ServerCapabilities

/**
 * MCP server over Streamable HTTP, available at http://localhost:[port]/mcp
 *
 * Only bound to the loopback interface : it's not reachable from the local network
 */
class FloconMcpServer(
    private val toolProviders: List<McpToolProvider>,
    private val port: Int = Constant.MCP_SERVER_PORT,
) : McpServerRepository {

    private var httpServer: EmbeddedServer<NettyApplicationEngine, NettyApplicationEngine.Configuration>? = null

    @Synchronized
    override fun startServer() {
        if (httpServer != null) return

        val mcpServer = createMcpServer()
        httpServer = embeddedServer(Netty, host = HOST, port = port) {
            mcpStatelessStreamableHttp(path = PATH) { mcpServer }
        }.start(wait = false)

        Logger.i(TAG) { "MCP server started on http://localhost:$port$PATH" }
    }

    @Synchronized
    fun stopServer() {
        httpServer?.stop()
        httpServer = null
    }

    internal fun createMcpServer(): Server = Server(
        serverInfo = Implementation(
            name = "flocon",
            title = "Flocon",
            version = SERVER_VERSION,
        ),
        options = ServerOptions(
            capabilities = ServerCapabilities(
                tools = ServerCapabilities.Tools(listChanged = false),
            ),
        ),
        instructions = INSTRUCTIONS,
    ) {
        toolProviders.forEach { it.register(this) }
    }

    private companion object {
        const val TAG = "FloconMcpServer"
        const val HOST = "127.0.0.1"
        const val PATH = "/mcp"
        const val SERVER_VERSION = "1.0.0"

        val INSTRUCTIONS = """
            Flocon is a debugging tool for mobile apps: the Flocon desktop app receives what happens in the mobile app
            connected to it (Android, iOS, KMP...).
            Use these tools to inspect the network calls made by the mobile app.
            - By default, tools target the device and app currently selected in the Flocon desktop app.
              Use list_devices to see the other devices / apps, then pass deviceId and packageName.
            - list_network_calls and search_network_calls return summaries; use get_network_call with a callId
              to get headers, bodies and the cURL command.
            - Network mocks make the app receive a fake response (or exception) instead of calling the server:
              create_network_mock with fromCallId starts from a captured call. Prefer disabling a mock
              (update_network_mock) over deleting it, and tell the user which mocks you left enabled.
            - Bad network simulation: list_bad_network_configs shows the configs (latency, random errors) and the active one,
              set_active_bad_network_config turns one on (only one at a time) or off. Always turn the simulation off
              when you are done and tell the user which config you left active.
            - Files of the app: list_files browses the "files" / "caches" directories, read_file returns a file content.
            - Analytics events sent by the app: count_analytics_events gives an overview per event name,
              list_analytics_events the events with their properties.
            - Feature flags of the app: list_feature_flags gives the provider, name and variant of each flag,
              get_feature_flag the full payload.
            - Databases of the app: list_databases gives the databaseId, list_database_tables the schema,
              query_database runs read only SQL queries (add a LIMIT), list_database_query_logs the queries run by the app.
            - SharedPreferences of the app: list_shared_preferences gives the sharedPreferenceId,
              get_shared_preference_values the typed keys / values of a file (optional text filter and limit).
            - Dashboards sent by the app: list_dashboards gives the ids, get_dashboard the containers and elements with their values.
            - Images loaded by the app: list_images gives their urls and load times.
            - Crashes reported by the app: list_crash_reports gives the exception type and message,
              get_crash_report the full stack trace.
            - Custom tables sent by the app (Table plugin): list_tables gives the tableId, list_table_items the rows.
        """.trimIndent()
    }
}
