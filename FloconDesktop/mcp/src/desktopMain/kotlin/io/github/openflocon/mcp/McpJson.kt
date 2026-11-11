package io.github.openflocon.mcp

import io.github.openflocon.domain.log.LogDomain
import io.github.openflocon.domain.log.LogManager
import io.modelcontextprotocol.kotlin.sdk.types.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.types.ToolAnnotations
import io.modelcontextprotocol.kotlin.sdk.types.error
import io.modelcontextprotocol.kotlin.sdk.types.success
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
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
 * Executes a tool: every failure is returned to the agent as an error result.
 * [McpToolException] messages are kept as is, any other failure (including JVM errors, which the MCP SDK
 * would otherwise send as a bare JSON-RPC error such as a class name) becomes an "internal error" message.
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
    } catch (e: McpToolException) {
        logManager.e(TAG, "$toolName failed: ${e.message}", null, domain = LogDomain.Mcp)
        CallToolResult.error(e.message.orEmpty())
    } catch (t: Throwable) {
        // the call itself was cancelled: nothing to answer. A cancellation leaking from the tool (ex: an inner timeout) is a failure
        if (t is CancellationException) currentCoroutineContext().ensureActive()
        logManager.e(TAG, "$toolName crashed", t, domain = LogDomain.Mcp)
        CallToolResult.error(internalErrorMessage(toolName, t))
    }
}

private fun internalErrorMessage(toolName: String, t: Throwable): String {
    val message = t.message?.lineSequence()?.firstOrNull { it.isNotBlank() }?.trim()?.take(MAX_REASON_LENGTH)
    val reason = listOfNotNull(t::class.simpleName ?: t::class.java.name, message).joinToString(": ")
    val hint = if (t is LinkageError) " Flocon Desktop could not load part of its code. Restart Flocon Desktop, then retry." else ""
    return "Internal Flocon Desktop error in $toolName: $reason.$hint"
}

private const val MAX_REASON_LENGTH = 200

private const val TAG = "McpTool"

private fun Set<String>.argumentsSuffix(): String = if (isEmpty()) "" else " (args: ${joinToString()})"

internal const val ARG_DEVICE_ID = "deviceId"
internal const val ARG_PACKAGE_NAME = "packageName"
