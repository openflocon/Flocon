package io.github.openflocon.flocon.plugins.network.mapper

import io.github.openflocon.flocon.FloconLogger
import org.json.JSONArray
import org.json.JSONObject

class WebSocketMockMessage(
    val id: String,
    val message: String,
)

internal fun webSocketIdsToJsonArray(ids: Collection<String>): JSONArray {
    val jsonArray = JSONArray()
    ids.forEach {
        jsonArray.put(it)
    }
    return jsonArray
}

internal fun parseWebSocketMockMessage(jsonString: String): WebSocketMockMessage? {
    try {
        val jsonObject = JSONObject(jsonString)
        return WebSocketMockMessage(
            id = jsonObject.getString("id"),
            message = jsonObject.getString("message"),
        )
    } catch (t: Throwable) {
        FloconLogger.logError(t.message ?: "mock wesocket network parsing issue", t)
    }
    return null
}