package io.github.openflocon.flocon.plugins.network.mapper

import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.core.FloconEncoder
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString

@Serializable
internal class WebSocketMockMessage(
    val id: String,
    val message: String,
)

internal fun webSocketIdsToJsonArray(ids: Collection<String>): String {
    return FloconEncoder.json.encodeToString(ids)
}

internal fun parseWebSocketMockMessage(jsonString: String): WebSocketMockMessage? {
    try {
        return FloconEncoder.json.decodeFromString<WebSocketMockMessage>(jsonString)
    } catch (t: Throwable) {
        FloconLogger.logError(t.message ?: "mock wesocket network parsing issue", t)
    }
    return null
}