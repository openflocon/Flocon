package io.github.openflocon.flocon.plugins.files.model.fromdevice

import org.json.JSONArray
import org.json.JSONObject

data class FilesResultDataModel(
    val requestId: String,
    val files: List<FileDataModel>,
) {
    fun toJson(): JSONObject {
        val json = JSONObject()

        json.put("requestId", requestId)
        json.put("files", JSONArray(
            files.map { it.toJson() })
        )

        return json
    }
}