package io.github.openflocon.mcp

import io.ktor.server.application.createApplicationPlugin
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readRemaining
import kotlinx.io.readByteArray
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.util.concurrent.atomic.AtomicReference

/**
 * Usage analytics of the MCP tools, implemented by the app shell (the mcp module knows nothing about telemetry).
 * Only the fixed name of a tool and the category of the client: never the arguments or the result of a call.
 */
fun interface McpUsageTracker {
    /** @param client one of [McpClients.ALLOWED] */
    fun toolCalled(tool: String, client: String)

    companion object {
        val None = McpUsageTracker { _, _ -> }
    }
}

object McpClients {
    const val UNKNOWN = "unknown"
    const val OTHER = "other"

    private val KNOWN = listOf("claude", "cursor", "codex", "windsurf", "vscode", "zed")
    val ALLOWED: Set<String> = (KNOWN + UNKNOWN + OTHER).toSet()

    /**
     * Category of an MCP client from the name it sent in the initialize handshake (ex: "claude-code", "Cursor").
     * Only the allow-list is returned: the raw name is free text from the client.
     */
    fun category(clientName: String?): String {
        val name = clientName?.trim()?.lowercase()?.takeIf { it.isNotEmpty() } ?: return UNKNOWN
        return when {
            name.contains("claude") -> "claude"
            name.contains("cursor") -> "cursor"
            name.contains("codex") -> "codex"
            name.contains("windsurf") -> "windsurf"
            name.contains("vscode") || name.contains("visual studio code") || name.contains("visual-studio-code") -> "vscode"
            name == "zed" || name.startsWith("zed-") || name.startsWith("zed ") -> "zed"
            else -> OTHER
        }
    }
}

/**
 * The endpoint is stateless: the client name is only sent in the `initialize` request, a tool call does not carry it.
 * The last client that said hello is remembered, which is accurate for the usual single MCP client.
 */
internal class McpClientNameHolder {
    private val lastClientName = AtomicReference<String?>(null)

    val category: String get() = McpClients.category(lastClientName.get())

    fun onRequestBody(body: String) {
        val name = initializeClientName(body) ?: return
        lastClientName.set(name)
    }
}

internal fun initializeClientName(body: String): String? {
    val root = runCatching { Json.parseToJsonElement(body) }.getOrNull() ?: return null
    val messages: List<JsonElement> = if (root is JsonArray) root else listOf(root)
    return messages.firstNotNullOfOrNull { message ->
        val obj = message as? JsonObject ?: return@firstNotNullOfOrNull null
        if (obj["method"]?.jsonPrimitive?.contentOrNull != "initialize") return@firstNotNullOfOrNull null
        obj["params"]?.jsonObject?.get("clientInfo")?.jsonObject?.get("name")?.jsonPrimitive?.contentOrNull
    }
}

/** Reads the body of the POST requests to remember the client of the `initialize` handshake, then hands it over untouched. */
internal fun mcpClientNamePlugin(holder: McpClientNameHolder) = createApplicationPlugin("McpClientName") {
    onCallReceive { _ ->
        transformBody { data ->
            if (data is ByteReadChannel) {
                val bytes = data.readRemaining().readByteArray()
                runCatching { holder.onRequestBody(bytes.decodeToString()) }
                ByteReadChannel(bytes)
            } else {
                data
            }
        }
    }
}
