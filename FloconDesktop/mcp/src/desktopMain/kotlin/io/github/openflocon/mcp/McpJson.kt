package io.github.openflocon.mcp

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
    toolName: String,
    argumentNames: Set<String>,
    block: suspend () -> CallToolResult,
): CallToolResult {
    val logger = McpLogs.logger
    val startedAt = TimeSource.Monotonic.markNow()
    return try {
        block().also { result ->
            val duration = startedAt.elapsedNow().inWholeMilliseconds
            if (result.isError == true) {
                logger?.error("$toolName returned an error in $duration ms", null)
            } else {
                logger?.info("$toolName ok in $duration ms${argumentNames.argumentsSuffix()}")
            }
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: McpToolException) {
        logger?.error("$toolName failed: ${e.message}", null)
        CallToolResult.error(e.message.orEmpty())
    } catch (t: Throwable) {
        logger?.error("$toolName crashed", t)
        throw t
    }
}

private fun Set<String>.argumentsSuffix(): String = if (isEmpty()) "" else " (args: ${joinToString()})"

internal const val ARG_DEVICE_ID = "deviceId"
internal const val ARG_PACKAGE_NAME = "packageName"
