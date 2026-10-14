package io.github.openflocon.mcp

import io.modelcontextprotocol.kotlin.sdk.types.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.types.ToolAnnotations
import io.modelcontextprotocol.kotlin.sdk.types.error
import io.modelcontextprotocol.kotlin.sdk.types.success
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json

internal val mcpOutputJson = Json {
    explicitNulls = false
    encodeDefaults = true
}

internal val readOnlyToolAnnotations = ToolAnnotations(
    readOnlyHint = true,
    destructiveHint = false,
    idempotentHint = true,
    openWorldHint = false,
)

/**
 * For the tools modifying the Flocon configuration (ex: mocks)
 */
internal fun writeToolAnnotations(
    destructive: Boolean,
    idempotent: Boolean,
) = ToolAnnotations(
    readOnlyHint = false,
    destructiveHint = destructive,
    idempotentHint = idempotent,
    openWorldHint = false,
)

internal inline fun <reified T> jsonResult(value: T): CallToolResult = CallToolResult.success(mcpOutputJson.encodeToString(value))

/**
 * Executes a tool, [McpToolException] are returned to the agent as an error result
 */
internal suspend fun safeToolCall(block: suspend () -> CallToolResult): CallToolResult = try {
    block()
} catch (e: CancellationException) {
    throw e
} catch (e: McpToolException) {
    CallToolResult.error(e.message.orEmpty())
}

internal const val ARG_DEVICE_ID = "deviceId"
internal const val ARG_PACKAGE_NAME = "packageName"
