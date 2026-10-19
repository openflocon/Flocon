package io.github.openflocon.mcp

import io.github.openflocon.domain.log.LogDomain
import io.github.openflocon.domain.log.LogManager
import io.modelcontextprotocol.kotlin.sdk.types.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.types.ToolAnnotations
import io.modelcontextprotocol.kotlin.sdk.types.error
import io.modelcontextprotocol.kotlin.sdk.types.success
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json
import kotlin.time.TimeSource

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
internal suspend fun safeToolCall(
    logManager: LogManager,
    toolName: String,
    argumentNames: Set<String>,
    block: suspend () -> CallToolResult,
): CallToolResult {
    val startedAt = TimeSource.Monotonic.markNow()
    return try {
        block().also { result ->
            val duration = startedAt.elapsedNow().inWholeMilliseconds
            if (result.isError == true) {
                logManager.e(TAG, "$toolName returned an error in $duration ms", null, domain = LogDomain.Mcp)
            } else {
                logManager.i(TAG, "$toolName ok in $duration ms${argumentNames.argumentsSuffix()}", domain = LogDomain.Mcp)
            }
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: McpToolException) {
        logManager.e(TAG, "$toolName failed: ${e.message}", null, domain = LogDomain.Mcp)
        CallToolResult.error(e.message.orEmpty())
    } catch (t: Throwable) {
        logManager.e(TAG, "$toolName crashed", t, domain = LogDomain.Mcp)
        throw t
    }
}

private const val TAG = "McpTool"

private fun Set<String>.argumentsSuffix(): String = if (isEmpty()) "" else " (args: ${joinToString()})"

internal const val ARG_DEVICE_ID = "deviceId"
internal const val ARG_PACKAGE_NAME = "packageName"
