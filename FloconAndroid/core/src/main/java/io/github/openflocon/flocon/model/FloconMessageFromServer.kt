package io.github.openflocon.flocon.model

import io.github.openflocon.flocon.FloconLogger
import org.json.JSONObject

data class FloconMessageFromServer(
    val plugin: String,
    val method: String,
    val body: String,
) {
    companion object {
        fun fromJson(
            message: String,
        ): FloconMessageFromServer? {
            return try {
                val jsonObject = JSONObject(message)

                FloconMessageFromServer(
                    plugin = jsonObject.getString("plugin"),
                    method = jsonObject.getString("method"),
                    body = jsonObject.getString("body"),
                )
            } catch (t: Throwable) {
                FloconLogger.logError("parsing issue", t)
                null
            }
        }

    }
}
