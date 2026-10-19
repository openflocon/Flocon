package io.github.openflocon.domain.mcp.logger

/**
 * Logs displayed in the "MCP" domain of the Flocon logs screen
 */
interface McpLogger {
    fun info(message: String)
    fun error(message: String, throwable: Throwable?)
}
