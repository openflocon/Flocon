package io.github.openflocon.mcp

import io.github.openflocon.domain.mcp.logger.McpLogger

internal object NoOpMcpLogger : McpLogger {
    override fun info(message: String) = Unit

    override fun error(message: String, throwable: Throwable?) = Unit
}
