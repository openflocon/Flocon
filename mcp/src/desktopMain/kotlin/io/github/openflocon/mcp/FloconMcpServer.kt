package io.github.openflocon.mcp

import co.touchlab.kermit.Logger
import io.github.openflocon.domain.Constant
import io.github.openflocon.domain.log.LogDomain
import io.github.openflocon.domain.log.LogManager
import io.github.openflocon.domain.mcp.repository.McpServerRepository
import io.ktor.server.application.install
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.server.ServerOptions
import io.modelcontextprotocol.kotlin.sdk.server.mcpStatelessStreamableHttp
import io.modelcontextprotocol.kotlin.sdk.types.Icon
import io.modelcontextprotocol.kotlin.sdk.types.Implementation
import io.modelcontextprotocol.kotlin.sdk.types.ServerCapabilities
import java.util.Base64

/**
 * MCP server over Streamable HTTP, available at http://localhost:[port]/mcp
 *
 * Only bound to the loopback interface : it's not reachable from the local network
 */
class FloconMcpServer(
    private val toolProviders: List<McpToolProvider>,
    private val port: Int = Constant.MCP_SERVER_PORT,
    private val logManager: LogManager,
    private val usageTracker: McpUsageTracker = McpUsageTracker.None,
) : McpServerRepository {

    private val clientNameHolder = McpClientNameHolder()

    private var httpServer: EmbeddedServer<NettyApplicationEngine, NettyApplicationEngine.Configuration>? = null

    @Synchronized
    override fun startServer() {
        if (httpServer != null) return

        val mcpServer = createMcpServer()
        httpServer = embeddedServer(Netty, host = HOST, port = port) {
            install(mcpClientNamePlugin(clientNameHolder))
            mcpStatelessStreamableHttp(path = PATH) { mcpServer }
        }.start(wait = false)

        Logger.i(TAG) { "MCP server started on http://localhost:$port$PATH" }
        logManager.i(TAG, "Server started on http://localhost:$port$PATH (${toolProviders.size} tool providers)", domain = LogDomain.Mcp)
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
            icons = serverIcons(),
        ),
        options = ServerOptions(
            capabilities = ServerCapabilities(
                tools = ServerCapabilities.Tools(listChanged = false),
            ),
        ),
        instructions = INSTRUCTIONS,
    ) {
        val toolServer = McpToolServer(
            server = this,
            logManager = logManager,
            usageTracker = usageTracker,
            clientCategory = { clientNameHolder.category },
        )
        toolProviders.forEach { it.register(toolServer) }
    }

    private fun serverIcons(): List<Icon>? {
        val bytes = FloconMcpServer::class.java.getResourceAsStream(ICON_RESOURCE)?.use { it.readBytes() }
            ?: return null
        return listOf(
            Icon(
                src = "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes),
                mimeType = "image/png",
                sizes = listOf("128x128"),
            ),
        )
    }

    private companion object {
        const val TAG = "FloconMcpServer"
        const val HOST = "127.0.0.1"
        const val PATH = "/mcp"
        const val SERVER_VERSION = "1.0.0"
        const val ICON_RESOURCE = "/flocon_icon.png"

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
            - Files of the app: list_file_roots lists the root folders announced by the app, list_files browses them, read_file returns a file content.
            - Analytics events sent by the app: count_analytics_events gives an overview per event name,
              list_analytics_events the events with their properties.
            - Feature flags of the app: list_feature_flags gives the provider, name and variant of each flag,
              get_feature_flag the full payload.
            - Accessibility events of the app (what TalkBack / VoiceOver receive): list_accessibility_events gives the focused
              elements and announcements, get_accessibility_state tells if a screen reader is running (use it when the list is empty),
              clear_accessibility_events resets the list.
            - Databases of the app: list_databases gives the databaseId, list_database_tables the schema,
              query_database runs read only SQL queries (add a LIMIT), list_database_query_logs the queries run by the app.
            - Deeplinks: list_deeplinks gives the deeplinks declared by the app with their parameters, list_deeplink_history those opened from Flocon,
              open_deeplink opens a deeplink on the device (this one acts on the device, the other ones are read only).
            - SharedPreferences of the app: list_shared_preferences gives the sharedPreferenceId,
              get_shared_preference_values the typed keys / values of a file (optional text filter and limit).
            - Dashboards sent by the app: list_dashboards gives the ids, get_dashboard the containers and elements with their values.
            - Images loaded by the app: list_images gives their urls and load times.
            - Crashes reported by the app: list_crash_reports gives the exception type and message,
              get_crash_report the full stack trace.
            - Custom tables sent by the app (Table plugin): list_tables gives the tableId, list_table_items the rows.
            - Screen of the device (Android, or the booted iOS Simulator): take_screenshot returns the image and its path,
              start_screen_recording / stop_screen_recording record a video (mp4). Files are saved in the Flocon documents folder,
              open_flocon_folder opens it on the computer and returns its path.
              Screenshot, recording and open_deeplink use adb on Android: they are not available for devices connected over wifi.
            - What the app did around a moment (before a crash, at 14:32, recently), above the network: get_timeline gives one narrative of every
              source with the anomalies first (crash, HTTP errors, loops, large responses) and tells which window it chose. Start there, then use
              the ids of its lines (network:<callId> with get_network_call, crash:<crashId> with get_crash_report). Its search parameter tells
              whether an event was sent. The times are those of the phone.
            - To show the user a zone of the timeline (a crash, an error, a loop), show_in_timeline opens it in Flocon, selects the event and centers
              the graph: give an event id of get_timeline, a time, or nothing. Flocon is never brought to the front: tell the user to go to the
              Flocon app. Its answer says if a filter of the user hides the event (the filters are never changed) and when the graph cannot
              draw the event (crashes only appear in the list view).
            - Server ports of the Flocon desktop (websocket default 9023, HTTP default 9024): get_server_ports gives the effective, saved and default
              ports and which one is forced by a launch option, set_server_ports changes them. Changing the ports restarts the servers and
              disconnects every device until the SDKs reconnect with the new ports: tell the user which ports you changed.
            - Flocon itself: take_flocon_desktop_screenshot returns an image of what the Flocon desktop app currently displays
              (not the device screen, nothing is saved).
              flocon_navigate_into_desktop_app moves it for the user: displays a screen, selects a device / app, sets the timeline
              view (graph or list). Flocon is never brought to the front: tell the user to go to the Flocon app to see it.
        """.trimIndent()
    }
}
