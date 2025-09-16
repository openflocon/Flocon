package io.github.openflocon.flocon.plugins.sharedprefs.model.fromdevice

import org.json.JSONArray
import org.json.JSONObject

internal data class SharedPreferenceValueResultDataModel(
    val requestId: String,
    val sharedPreferenceName: String,
    val rows: List<SharedPreferenceRowDataModel>,
) {
    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("requestId", requestId)
            put("sharedPreferenceName", sharedPreferenceName)
            put(
                "rows", JSONArray(
                    rows.map { it.toJson() })
            )
        }
    }
}