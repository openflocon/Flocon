<<<<<<<< HEAD:FloconAndroid/network/core/src/commonMain/kotlin/io/github/openflocon/flocon/network/core/mapper/Websocket.kt
package io.github.openflocon.flocon.network.core.mapper
========
package io.github.openflocon.flocon.network.core.noop.mapper
>>>>>>>> 2.0.0:FloconAndroid/network/core-no-op/src/commonMain/kotlin/io/github/openflocon/flocon/network/core/noop/mapper/Websocket.kt

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