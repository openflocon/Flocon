package com.github.openflocon.flocon.plugins.files.model.fromdevice

import org.json.JSONObject

data class FileDataModel(
    val name: String,
    val isDirectory: Boolean,
    val path: String,
    val size: Long,
    val lastModified: Long,
) {
    fun toJson() : JSONObject {
        val json = JSONObject()

        json.put("name", name)
        json.put("isDirectory", isDirectory)
        json.put("path", path)
        json.put("size", size)
        json.put("lastModified", lastModified)

        return json
    }
}