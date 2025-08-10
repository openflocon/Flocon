package io.github.openflocon.flocon.model

import io.github.openflocon.flocon.FloconLogger
import org.json.JSONObject

fun floconMessageFromServerFromJson(
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