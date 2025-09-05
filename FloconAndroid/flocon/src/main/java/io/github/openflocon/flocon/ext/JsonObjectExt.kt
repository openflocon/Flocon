package io.github.openflocon.flocon.ext


import io.github.openflocon.flocon.FloconLogger
import org.json.JSONException
import org.json.JSONObject

/** Convert JSONObject to Map<String, String> */
fun JSONObject.toMap(): Map<String, String> {
    val map = mutableMapOf<String, String>()
    val keys = this.keys()
    while (keys.hasNext()) {
        val key = keys.next()
        try {
            val value = this.getString(key)
            map[key] = value
        } catch (e: JSONException) {
            FloconLogger.logError("Error converting JSONObject value to String for key: $key", e)
        }
    }
    return map
}