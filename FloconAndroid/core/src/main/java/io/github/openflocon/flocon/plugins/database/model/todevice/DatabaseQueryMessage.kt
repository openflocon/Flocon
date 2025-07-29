package io.github.openflocon.flocon.plugins.database.model.todevice

import io.github.openflocon.flocon.FloconLogger
import org.json.JSONObject

data class DatabaseQueryMessage(
    val query: String,
    val requestId: String,
    val database: String,
) {
    companion object {
        fun fromJson(message: String): DatabaseQueryMessage? {
            return try {
                val jsonObject = JSONObject(message)

                val query = jsonObject.getString("query")
                val requestId = jsonObject.getString("requestId")
                val database = jsonObject.getString("database")

                DatabaseQueryMessage(
                    query = query,
                    requestId = requestId,
                    database = database
                )
            }  catch (t: Throwable) {
                FloconLogger.logError("parsing issue", t)
                null
            }
        }
    }
}