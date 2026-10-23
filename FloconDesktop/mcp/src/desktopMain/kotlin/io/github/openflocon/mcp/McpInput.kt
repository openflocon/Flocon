package io.github.openflocon.mcp

import io.modelcontextprotocol.kotlin.sdk.types.CallToolRequest
import io.modelcontextprotocol.kotlin.sdk.types.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.types.ToolAnnotations
import io.modelcontextprotocol.kotlin.sdk.types.ToolSchema
import kotlinx.schema.generator.json.FunctionCallingSchemaConfig
import kotlinx.schema.generator.json.TypeGraphToFunctionCallingSchemaTransformer
import kotlinx.schema.generator.json.serialization.SerializationClassSchemaIntrospector
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.serializer

/**
 * The arguments of a tool are a @[Serializable] class: its JSON schema is generated from it by kotlinx-schema,
 * and the arguments are decoded into it.
 *
 * - a property without default value is required
 * - [McpDescription] describes the property to the AI agent
 * - enums are listed with their [SerialName]
 */
@OptIn(ExperimentalSerializationApi::class)
@SerialInfo
@Target(AnnotationTarget.PROPERTY)
annotation class McpDescription(val value: String)

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
internal inline fun <reified I : Any, reified O> McpToolServer.addTypedTool(
    name: String,
    title: String,
    description: String,
    annotations: ToolAnnotations,
    crossinline handler: suspend (I) -> O,
) {
    addTypedToolWithResult<I>(name = name, title = title, description = description, annotations = annotations) { input ->
        jsonResult(handler(input))
    }
}

/**
 * Same as [addTypedTool], for the tools building their result themselves (ex: an image)
 */
internal inline fun <reified I : Any> McpToolServer.addTypedToolWithResult(
    name: String,
    title: String,
    description: String,
    annotations: ToolAnnotations,
    crossinline handler: suspend (I) -> CallToolResult,
) {
    addLoggedTool(
        name = name,
        title = title,
        description = description,
        inputSchema = toolSchemaOf<I>(),
        annotations = annotations,
    ) { request ->
        handler(request.decodeArguments<I>())
    }
}

internal inline fun <reified I : Any> toolSchemaOf(): ToolSchema = serializer<I>().descriptor.toToolSchema()

/**
 * Function calling schema: flat, without `$defs` / `$ref` that some MCP clients don't resolve
 */
private val schemaGenerator = TypeGraphToFunctionCallingSchemaTransformer(
    // only the properties without default value are required
    config = FunctionCallingSchemaConfig(strictMode = false),
)

private val schemaIntrospector = SerializationClassSchemaIntrospector(
    config = SerializationClassSchemaIntrospector.Config(
        descriptionExtractor = { annotations -> annotations.filterIsInstance<McpDescription>().firstOrNull()?.value },
    ),
)

internal fun SerialDescriptor.toToolSchema(): ToolSchema {
    val parameters = Json.encodeToJsonElement(
        schemaGenerator.transform(schemaIntrospector.introspect(this), serialName).parameters,
    ).jsonObject
    return ToolSchema(
        properties = parameters["properties"]?.withoutNullTypes()?.jsonObject,
        required = parameters["required"]?.jsonArray?.map { it.jsonPrimitive.content }?.takeIf { it.isNotEmpty() },
    )
}

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

/**
 * `"type": ["string", "null"]` becomes `"type": "string"`: Gemini rejects the union types,
 * and the optional properties are already absent from `required`
 */
private fun JsonElement.withoutNullTypes(): JsonElement = when (this) {
    is JsonObject -> JsonObject(
        mapValues { (key, value) ->
            val types = (value as? JsonArray)?.filterNot { it.jsonPrimitive.contentOrNull == "null" }
            if (key == "type" && types?.size == 1) types.single() else value.withoutNullTypes()
        },
    )

    is JsonArray -> JsonArray(map { it.withoutNullTypes() })

    is JsonPrimitive -> this
}
