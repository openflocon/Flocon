package io.github.openflocon.flocon.plugins.network.mapper

import io.github.openflocon.flocon.FloconLogger
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
internal class WebSocketMockMessage(
    val id: String,
    val message: String,
)

internal fun webSocketIdsToJsonArray(json: Json, ids: Collection<String>): String {
    return json.encodeToString(ids)
}

internal fun parseWebSocketMockMessage(json: Json, jsonString: String): WebSocketMockMessage? {
    try {
        return json.decodeFromString<WebSocketMockMessage>(jsonString)
    } catch (t: Throwable) {
        FloconLogger.logError(t.message ?: "mock wesocket network parsing issue", t)
    }
    return null
}