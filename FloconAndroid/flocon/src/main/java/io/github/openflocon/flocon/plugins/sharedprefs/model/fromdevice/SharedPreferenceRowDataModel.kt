package io.github.openflocon.flocon.plugins.sharedprefs.model.fromdevice

import org.json.JSONArray
import org.json.JSONObject

data class SharedPreferenceRowDataModel(
    val key: String,
    val stringValue: String? = null,
    val intValue: Int? = null,
    val floatValue: Float? = null,
    val booleanValue: Boolean? = null,
    val longValue: Long? = null,
    val setStringValue: Set<String>? = null,
) {
    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("key", key)
            stringValue?.let { put("stringValue", it) }
            intValue?.let { put("intValue", it) }
            floatValue?.let { put("floatValue", it) }
            booleanValue?.let { put("booleanValue", it) }
            longValue?.let { put("longValue", it) }
            setStringValue?.let { put("setStringValue", JSONArray(it)) }
        }
    }
}