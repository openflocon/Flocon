package io.github.openflocon.mcp

/**
 * Registers the MCP tools of one Flocon feature (network, devices, ...).
 *
 * To expose a new feature to AI agents, implement this interface
 * and bind it as [McpToolProvider] in [mcpModule].
 */
interface McpToolProvider {
    fun register(server: McpToolServer)
}
