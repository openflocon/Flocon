package io.github.openflocon.mcp

import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.types.CallToolRequest
import io.modelcontextprotocol.kotlin.sdk.types.ToolAnnotations
import io.modelcontextprotocol.kotlin.sdk.types.ToolSchema
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.serializer

/**
 * The arguments of a tool are a @[Serializable] class: its JSON schema is generated from it, and the arguments are decoded into it.
 *
 * - a property without default value is required
 * - [McpDescription] and [McpRange] describe the property to the AI agent
 * - enums are listed with their [SerialName]
 */
@OptIn(ExperimentalSerializationApi::class)
@SerialInfo
@Target(AnnotationTarget.PROPERTY)
annotation class McpDescription(val value: String)

@OptIn(ExperimentalSerializationApi::class)
@SerialInfo
@Target(AnnotationTarget.PROPERTY)
annotation class McpRange(
    val min: Int = Int.MIN_VALUE,
    val max: Int = Int.MAX_VALUE,
)

/**
 * Arguments of the tools targeting a device / app, by default the one selected in Flocon
 */
internal interface McpTargetArguments {
    val deviceId: String?
    val packageName: String?
}

internal const val DEVICE_ID_DESCRIPTION = "Device id (see list_devices). Defaults to the device currently selected in Flocon."
internal const val PACKAGE_NAME_DESCRIPTION =
    "Application package name / bundle id (see list_devices). Defaults to the app currently selected in Flocon."

@Serializable
internal enum class McpOrder {
    @SerialName("newest_first")
    NewestFirst,

    @SerialName("oldest_first")
    OldestFirst,
}

/**
 * Registers a tool whose arguments are decoded into [I], and whose result [O] is returned as JSON
 */
internal inline fun <reified I : Any, reified O> Server.addTypedTool(
    name: String,
    title: String,
    description: String,
    annotations: ToolAnnotations,
    crossinline handler: suspend (I) -> O,
) {
    addTool(
        name = name,
        title = title,
        description = description,
        inputSchema = toolSchemaOf<I>(),
        toolAnnotations = annotations,
    ) { request ->
        safeToolCall { jsonResult(handler(request.decodeArguments<I>())) }
    }
}

internal inline fun <reified I : Any> toolSchemaOf(): ToolSchema = serializer<I>().descriptor.toToolSchema()

internal val mcpInputJson = Json {
    ignoreUnknownKeys = true
    isLenient = true
}

/**
 * @throws McpToolException explaining which argument is invalid
 */
internal inline fun <reified I : Any> CallToolRequest.decodeArguments(): I {
    // a null argument is a missing one: its default value is used
    val arguments = JsonObject(arguments.orEmpty().filterValues { it !is JsonNull })
    return try {
        mcpInputJson.decodeFromJsonElement(serializer<I>(), arguments)
    } catch (e: IllegalArgumentException) {
        throw McpToolException("Invalid arguments: ${e.message}")
    }
}

@OptIn(ExperimentalSerializationApi::class)
internal fun SerialDescriptor.toToolSchema(): ToolSchema {
    require(kind == StructureKind.CLASS || kind == StructureKind.OBJECT) { "$serialName must be a class" }
    return ToolSchema(
        properties = propertiesSchema(),
        required = requiredProperties().takeIf { it.isNotEmpty() },
    )
}

@OptIn(ExperimentalSerializationApi::class)
private fun SerialDescriptor.requiredProperties(): List<String> = (0 until elementsCount)
    .filterNot { isElementOptional(it) }
    .map { getElementName(it) }

@OptIn(ExperimentalSerializationApi::class)
private fun SerialDescriptor.propertiesSchema(): JsonObject = buildJsonObject {
    for (index in 0 until elementsCount) {
        val annotations = getElementAnnotations(index)
        put(
            getElementName(index),
            getElementDescriptor(index).schema(
                description = annotations.filterIsInstance<McpDescription>().firstOrNull()?.value,
                range = annotations.filterIsInstance<McpRange>().firstOrNull(),
            ),
        )
    }
}

@OptIn(ExperimentalSerializationApi::class)
private fun SerialDescriptor.schema(
    description: String? = null,
    range: McpRange? = null,
): JsonObject = buildJsonObject {
    when (val kind = kind) {
        PrimitiveKind.STRING, PrimitiveKind.CHAR -> put("type", "string")
        PrimitiveKind.BOOLEAN -> put("type", "boolean")
        PrimitiveKind.BYTE, PrimitiveKind.SHORT, PrimitiveKind.INT, PrimitiveKind.LONG -> put("type", "integer")
        PrimitiveKind.FLOAT, PrimitiveKind.DOUBLE -> put("type", "number")
        SerialKind.ENUM -> {
            put("type", "string")
            putJsonArray("enum") { (0 until elementsCount).forEach { add(getElementName(it)) } }
        }

        StructureKind.LIST -> {
            put("type", "array")
            put("items", getElementDescriptor(0).schema())
        }

        StructureKind.MAP -> {
            put("type", "object")
            put("additionalProperties", getElementDescriptor(1).schema())
        }

        StructureKind.CLASS, StructureKind.OBJECT -> {
            put("type", "object")
            put("properties", propertiesSchema())
            requiredProperties().takeIf { it.isNotEmpty() }?.let { required ->
                putJsonArray("required") { required.forEach { add(it) } }
            }
        }

        is PolymorphicKind, SerialKind.CONTEXTUAL -> error("$serialName: $kind arguments are not supported")
    }
    description?.let { put("description", it) }
    range?.min?.takeIf { it != Int.MIN_VALUE }?.let { put("minimum", it) }
    range?.max?.takeIf { it != Int.MAX_VALUE }?.let { put("maximum", it) }
}
