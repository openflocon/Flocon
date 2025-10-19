package io.github.openflocon.flocon.model

import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.core.FloconEncoder
import kotlinx.serialization.Serializable

internal fun floconMessageFromServerFromJson(
    message: String,
): FloconMessageFromServer? {
    return try {
        FloconEncoder.json.decodeFromString<FloconMessageFromServer>(message)
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