package io.github.openflocon.mcp.serverports

import io.github.openflocon.domain.Constant
import io.github.openflocon.domain.models.settings.ServerPortsDomainModel
import io.github.openflocon.domain.models.settings.ServerPortsOverrides
import io.github.openflocon.domain.settings.repository.SettingsRepository
import io.github.openflocon.domain.settings.usecase.ObserveServerPortsUseCase
import io.github.openflocon.domain.settings.usecase.ServerPortsError
import io.github.openflocon.domain.settings.usecase.UpdateServerPortsUseCase
import io.github.openflocon.mcp.McpToolException
import io.github.openflocon.mcp.McpToolProvider
import io.github.openflocon.mcp.McpToolServer
import io.github.openflocon.mcp.addTypedTool
import io.github.openflocon.mcp.jsonResult
import io.github.openflocon.mcp.readOnlyToolAnnotations
import io.github.openflocon.mcp.writeToolAnnotations
import io.modelcontextprotocol.kotlin.sdk.types.ToolSchema
import kotlinx.coroutines.flow.first

internal class ServerPortsMcpToolProvider(
    private val observeServerPorts: ObserveServerPortsUseCase,
    private val updateServerPorts: UpdateServerPortsUseCase,
    private val settingsRepository: SettingsRepository,
    private val overrides: ServerPortsOverrides,
) : McpToolProvider {

    override fun register(server: McpToolServer) {
        server.addLoggedTool(
            name = "get_server_ports",
            title = "Get server ports",
            description = "Gives the ports of the Flocon desktop servers the mobile SDKs connect to: the websocket port (default 9023) " +
                "and the HTTP port (default 9024). Returns the effective ports, the saved ones, the defaults, " +
                "and whether each port is forced by a launch option for this session (then it cannot be changed with set_server_ports).",
            inputSchema = ToolSchema(),
            annotations = readOnlyToolAnnotations,
        ) {
            jsonResult(getServerPorts())
        }

        server.addTypedTool(
            name = "set_server_ports",
            title = "Set server ports",
            description = "Changes the websocket and / or HTTP port of the Flocon desktop servers (saved in the Flocon settings). " +
                "At least one port is required, the omitted one keeps its current value; 9023 / 9024 put the defaults back. " +
                "Ports must be in 1024..65535, different, not 9042 (MCP server), the websocket port cannot be 9024 " +
                "and the HTTP port cannot be 9023. " +
                "WARNING: applying the ports restarts the websocket and HTTP servers and disconnects every device " +
                "until the SDKs reconnect (the SDK must use the new ports). The MCP server (port 9042) is not affected. " +
                "A port forced by a launch option cannot be changed (error).",
            annotations = writeToolAnnotations(destructive = false, idempotent = true),
            handler = ::setServerPorts,
        )
    }

    private suspend fun getServerPorts(): ServerPortsMcpResult {
        val effective = observeServerPorts().first()
        val saved = settingsRepository.savedServerPorts.first()
        return ServerPortsMcpResult(
            effective = effective.toMcp(),
            saved = saved.toMcp(),
            defaults = ServerPortsDomainModel().toMcp(),
            websocketPortForcedByLaunchOption = overrides.websocketPort != null,
            httpPortForcedByLaunchOption = overrides.httpPort != null,
            mcpPort = Constant.MCP_SERVER_PORT,
        )
    }

    private suspend fun setServerPorts(input: SetServerPortsInput): SetServerPortsMcpResult {
        if (input.websocketPort == null && input.httpPort == null) {
            throw McpToolException("Provide at least one of websocketPort and httpPort")
        }
        if (input.websocketPort != null && overrides.websocketPort != null) {
            throw McpToolException(
                "The websocket port is forced to ${overrides.websocketPort} by a launch option for this session, it cannot be changed",
            )
        }
        if (input.httpPort != null && overrides.httpPort != null) {
            throw McpToolException(
                "The HTTP port is forced to ${overrides.httpPort} by a launch option for this session, it cannot be changed",
            )
        }
        val current = observeServerPorts().first()
        val requested = ServerPortsDomainModel(
            websocketPort = input.websocketPort ?: current.websocketPort,
            httpPort = input.httpPort ?: current.httpPort,
        )
        updateServerPorts(requested).fold(
            doOnFailure = { throw McpToolException(it.toMessage(requested)) },
            doOnSuccess = {},
        )
        val effective = observeServerPorts().first()
        return SetServerPortsMcpResult(
            effective = effective.toMcp(),
            message = "Server ports are now websocket ${effective.websocketPort} and HTTP ${effective.httpPort}. " +
                "The websocket and HTTP servers restart: every device is disconnected until the SDKs reconnect " +
                "(they must use these ports). The MCP server is not affected.",
        )
    }

    private fun ServerPortsError.toMessage(ports: ServerPortsDomainModel) = when (this) {
        ServerPortsError.OutOfRange ->
            "Ports must be between ${UpdateServerPortsUseCase.ALLOWED_RANGE.first} and ${UpdateServerPortsUseCase.ALLOWED_RANGE.last} " +
                "(websocket ${ports.websocketPort}, HTTP ${ports.httpPort})"

        ServerPortsError.SamePorts ->
            "The websocket and HTTP ports must be different (both ${ports.websocketPort})"

        ServerPortsError.McpPortReserved ->
            "Port ${Constant.MCP_SERVER_PORT} is used by the MCP server"

        ServerPortsError.ReservedDefaultPort ->
            "Port ${Constant.SERVER_HTTP_PORT} can only be the HTTP port and port ${Constant.SERVER_WEBSOCKET_PORT} " +
                "can only be the websocket port (default adb reverse mappings)"
    }

    private fun ServerPortsDomainModel.toMcp() = ServerPortsMcpModel(websocketPort = websocketPort, httpPort = httpPort)
}
