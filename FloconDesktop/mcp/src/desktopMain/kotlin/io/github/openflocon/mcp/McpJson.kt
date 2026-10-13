package io.github.openflocon.mcp

import io.modelcontextprotocol.kotlin.sdk.types.CallToolRequest
import io.modelcontextprotocol.kotlin.sdk.types.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.types.ToolAnnotations
import io.modelcontextprotocol.kotlin.sdk.types.error
import io.modelcontextprotocol.kotlin.sdk.types.success
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.add
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject

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

// region arguments

private fun CallToolRequest.argument(name: String): JsonElement? = arguments?.get(name)?.takeUnless { it is JsonNull }

internal fun CallToolRequest.stringArg(name: String): String? = (argument(name) as? JsonPrimitive)?.contentOrNull?.takeIf { it.isNotBlank() }

internal fun CallToolRequest.requiredStringArg(name: String): String = stringArg(name) ?: throw McpToolException("Missing required argument '$name'")

internal fun CallToolRequest.longArg(name: String): Long? = argument(name)?.let {
    (it as? JsonPrimitive)?.doubleOrNull?.toLong() ?: throw McpToolException("Argument '$name' must be a number")
}

internal fun CallToolRequest.intArg(name: String): Int? = longArg(name)?.toInt()

internal fun CallToolRequest.booleanArg(name: String): Boolean? = argument(name)?.let {
    (it as? JsonPrimitive)?.booleanOrNull ?: throw McpToolException("Argument '$name' must be a boolean")
}

internal fun CallToolRequest.stringListArg(name: String): List<String>? = when (val value = argument(name)) {
    null -> null
    is JsonArray -> value.mapNotNull { it.jsonPrimitive.contentOrNull?.takeIf(String::isNotBlank) }
    is JsonPrimitive -> listOfNotNull(value.contentOrNull?.takeIf(String::isNotBlank))
    else -> throw McpToolException("Argument '$name' must be an array of strings")
}

internal fun CallToolRequest.intListArg(name: String): List<Int>? = when (val value = argument(name)) {
    null -> null
    is JsonArray -> value.map {
        it.jsonPrimitive.doubleOrNull?.toInt() ?: throw McpToolException("Argument '$name' must be an array of integers")
    }

    is JsonPrimitive -> listOf(value.doubleOrNull?.toInt() ?: throw McpToolException("Argument '$name' must be an array of integers"))
    else -> throw McpToolException("Argument '$name' must be an array of integers")
}

// endregion

// region schema

internal fun JsonObjectBuilder.stringProperty(
    name: String,
    description: String,
    enum: List<String>? = null,
) {
    putJsonObject(name) {
        put("type", "string")
        put("description", description)
        enum?.let { values -> putJsonArray("enum") { values.forEach { add(it) } } }
    }
}

internal fun JsonObjectBuilder.integerProperty(
    name: String,
    description: String,
    minimum: Long? = null,
    maximum: Long? = null,
) {
    putJsonObject(name) {
        put("type", "integer")
        put("description", description)
        minimum?.let { put("minimum", it) }
        maximum?.let { put("maximum", it) }
    }
}

internal fun JsonObjectBuilder.booleanProperty(
    name: String,
    description: String,
) {
    putJsonObject(name) {
        put("type", "boolean")
        put("description", description)
    }
}

internal fun JsonObjectBuilder.arrayProperty(
    name: String,
    description: String,
    itemType: String,
    enum: List<String>? = null,
) {
    putJsonObject(name) {
        put("type", "array")
        put("description", description)
        putJsonObject("items") {
            put("type", itemType)
            enum?.let { values -> putJsonArray("enum") { values.forEach { add(it) } } }
        }
    }
}

internal fun JsonObjectBuilder.targetProperties() {
    stringProperty(
        name = ARG_DEVICE_ID,
        description = "Device id (see list_devices). Defaults to the device currently selected in Flocon.",
    )
    stringProperty(
        name = ARG_PACKAGE_NAME,
        description = "Application package name / bundle id (see list_devices). Defaults to the app currently selected in Flocon.",
    )
}

internal fun properties(block: JsonObjectBuilder.() -> Unit): JsonObject = buildJsonObject(block)

internal const val ARG_DEVICE_ID = "deviceId"
internal const val ARG_PACKAGE_NAME = "packageName"

// endregion
