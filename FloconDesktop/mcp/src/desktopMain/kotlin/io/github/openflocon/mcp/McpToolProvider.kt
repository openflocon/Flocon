package io.github.openflocon.mcp

import io.modelcontextprotocol.kotlin.sdk.server.Server

/**
 * Registers the MCP tools of one Flocon feature (network, devices, ...).
 *
 * To expose a new feature to AI agents, implement this interface
 * and bind it as [McpToolProvider] in [mcpModule].
 */
interface McpToolProvider {
    fun register(server: Server)
}
