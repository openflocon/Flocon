package io.github.openflocon.flocondesktop.common.log

import io.github.openflocon.domain.mcp.logger.McpLogger

class McpLoggerImpl(
    private val logManager: LogManager,
) : McpLogger {

    override fun info(message: String) {
        logManager.i(TAG, message, LogDomain.Mcp)
    }

    override fun error(message: String, throwable: Throwable?) {
        logManager.e(TAG, message, throwable, LogDomain.Mcp)
    }

    private companion object {
        const val TAG = "MCP"
    }
}
