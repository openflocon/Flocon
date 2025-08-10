package io.github.openflocon.flocon.plugins.database.model.fromdevice

import org.json.JSONObject

data class QueryResultDataModel(
    val requestId: String,
    val result: String,
) {
    fun toJson(): JSONObject {
        val json = JSONObject()

        json.put("requestId", requestId)
        json.put("result", result)

        return json
    }
}