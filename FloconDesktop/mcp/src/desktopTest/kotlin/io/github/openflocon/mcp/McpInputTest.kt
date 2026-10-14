package io.github.openflocon.mcp

import io.modelcontextprotocol.kotlin.sdk.types.CallToolRequest
import io.modelcontextprotocol.kotlin.sdk.types.CallToolRequestParams
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class McpInputTest {

    @Serializable
    internal data class SampleInput(
        @McpDescription("The id.")
        val id: String,
        @McpDescription("Max items.")
        val limit: Int = 5,
        val enabled: Boolean? = null,
        val order: McpOrder = McpOrder.NewestFirst,
        val codes: List<Int>? = null,
        val headers: Map<String, String>? = null,
        val ratio: Double? = null,
    )

    @Test
    fun `the schema is generated from the class, flat and without null types`() {
        val schema = toolSchemaOf<SampleInput>()

        assertEquals(listOf("id"), schema.required)
        assertEquals(
            Json.parseToJsonElement(
                """
                {
                  "id": {"type": "string", "description": "The id."},
                  "limit": {"type": "integer", "description": "Max items."},
                  "enabled": {"type": "boolean"},
                  "order": {"type": "string", "enum": ["newest_first", "oldest_first"]},
                  "codes": {"type": "array", "items": {"type": "integer"}},
                  "headers": {"type": "object", "additionalProperties": {"type": "string"}},
                  "ratio": {"type": "number"}
                }
                """,
            ).jsonObject,
            schema.properties,
        )
    }

    @Test
    fun `the arguments are decoded, missing and null ones use the default value`() {
        val input = request {
            put("id", "42")
            put("enabled", null as String?)
            put("order", "oldest_first")
            putJsonArray("codes") {
                add(JsonPrimitive(401))
            }
            put("unknown", "ignored")
        }.decodeArguments<SampleInput>()

        assertEquals(SampleInput(id = "42", order = McpOrder.OldestFirst, codes = listOf(401)), input)
    }

    @Test
    fun `invalid arguments are explained to the agent`() {
        val missing = assertFailsWith<McpToolException> { request {}.decodeArguments<SampleInput>() }
        assertTrue(missing.message!!.contains("id"), missing.message)

        val wrongEnum = assertFailsWith<McpToolException> {
            request {
                put("id", "42")
                put("order", "random")
            }.decodeArguments<SampleInput>()
        }
        assertTrue(wrongEnum.message!!.contains("random"), wrongEnum.message)

        assertFailsWith<McpToolException> {
            request {
                put("id", "42")
                put("limit", "not a number")
            }.decodeArguments<SampleInput>()
        }
    }

    private fun request(arguments: JsonObjectBuilder.() -> Unit) = CallToolRequest(
        CallToolRequestParams(name = "tool", arguments = buildJsonObject(arguments)),
    )
}
