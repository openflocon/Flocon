package io.github.openflocon.mcp

import io.github.openflocon.domain.log.LogManager
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.types.CallToolRequest
import io.modelcontextprotocol.kotlin.sdk.types.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.types.ToolAnnotations
import io.modelcontextprotocol.kotlin.sdk.types.ToolSchema

/**
 * A [Server] on which the tools are registered: every call of a tool is logged in the Flocon logs
 */
class McpToolServer internal constructor(
    private val server: Server,
    internal val logManager: LogManager,
    private val usageTracker: McpUsageTracker = McpUsageTracker.None,
    private val clientCategory: () -> String = { McpClients.UNKNOWN },
) {

    internal fun addLoggedTool(
        name: String,
        title: String,
        description: String,
        inputSchema: ToolSchema,
        annotations: ToolAnnotations,
        handler: suspend (CallToolRequest) -> CallToolResult,
    ) {
        server.addTool(
            name = name,
            title = title,
            description = description,
            inputSchema = inputSchema,
            toolAnnotations = annotations,
        ) { request ->
            // only the fixed name of the tool and the category of the client
            runCatching { usageTracker.toolCalled(tool = name, client = clientCategory()) }
            safeToolCall(
                logManager = logManager,
                toolName = name,
                argumentNames = request.arguments?.keys.orEmpty(),
            ) { handler(request) }
        }
    }
}
