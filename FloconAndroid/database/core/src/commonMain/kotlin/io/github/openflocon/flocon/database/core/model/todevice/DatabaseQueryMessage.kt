package io.github.openflocon.flocon.database.core.model.todevice

import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.core.FloconEncoder
import kotlinx.serialization.Serializable

@Serializable
data class DatabaseQueryMessage(
    val query: String,
    val requestId: String,
    val database: String,
) {
    companion object {
        fun fromJson(message: String): DatabaseQueryMessage? {
            return try {
                FloconEncoder.json.decodeFromString<DatabaseQueryMessage>(message)
            } catch (t: Throwable) {
                FloconLogger.logError("parsing issue", t)
                null
            }
        }
    }
}
