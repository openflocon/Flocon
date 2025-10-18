package io.github.openflocon.flocon.model

import io.github.openflocon.flocon.FloconLogger
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

internal fun floconMessageFromServerFromJson(
    json: Json,
    message: String,
): FloconMessageFromServer? {
    return try {
        json.decodeFromString<FloconMessageFromServer>(message)
    } catch (t: Throwable) {
        FloconLogger.logError("parsing issue", t)
        null
    }
}

@Serializable
internal data class FloconMessageFromServer(
    val plugin: String,
    val method: String,
    val body: String,
)