package io.github.openflocon.flocondesktop.common.ui

import co.touchlab.kermit.Logger
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

object JsonPrettyPrinter {
    // Configure a Json instance for pretty printing
    private val json =
        Json {
            prettyPrint = true // This is the key setting for indentation
            encodeDefaults = true // Include default values during serialization
            isLenient = true // Allow some non-strict JSON (e.g., unquoted keys, comments)
            ignoreUnknownKeys = true // Ignore JSON keys that are not present in your data classes
        }

    /**
     * Attempts to pretty-print a given JSON string.
     * If the string is not valid JSON, it returns the original string.
     *
     * @param jsonString The input JSON string.
     * @return A pretty-printed JSON string, or the original string if parsing fails.
     */
    fun prettyPrint(jsonString: String): String {
        if (jsonString.isBlank()) {
            return jsonString // Return empty/blank string as is
        }
        return try {
            // Attempt to parse the string into a JsonElement
            val parsedJson = json.parseToJsonElement(jsonString)
            // Encode it back to a string with prettyPrint enabled
            json.encodeToString(JsonElement.serializer(), parsedJson)
        } catch (e: Exception) {
            Logger.e(e) { "Failed to pretty-print JSON" }
            // If parsing fails (e.g., not valid JSON), return the original string
            jsonString
        }
    }
}
