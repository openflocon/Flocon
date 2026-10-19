package io.github.openflocon.mcp

import io.github.openflocon.domain.mcp.logger.McpLogger

/**
 * The tool helpers are top level functions, they can't receive the logger by injection
 */
internal object McpLogs {
    @Volatile
    var logger: McpLogger? = null
}
