package io.github.openflocon.flocon.plugins.database.model.fromdevice

import org.json.JSONArray
import org.json.JSONObject

sealed interface DatabaseExecuteSqlResponse {

    // Case for successful SELECT queries
    data class Select(
        val columns: List<String>,
        val values: List<List<String?>>
    ) : DatabaseExecuteSqlResponse

    // Case for successful INSERT queries
    data class Insert(
        val insertedId: Long
    ) : DatabaseExecuteSqlResponse

    // Case for successful UPDATE or DELETE queries
    data class UpdateDelete(
        val affectedCount: Int
    ) : DatabaseExecuteSqlResponse

    // Case for successful "raw" queries (CREATE TABLE, DROP TABLE, etc.)
    object RawSuccess : DatabaseExecuteSqlResponse

    // Case for an SQL execution error
    data class Error(
        val message: String,       // Detailed error message
        val originalSql: String,   // SQL query that caused the error (optional)
    ) : DatabaseExecuteSqlResponse
}

fun DatabaseExecuteSqlResponse.toJson(): JSONObject {
    val json = JSONObject()
    when (this) {
        is DatabaseExecuteSqlResponse.Error -> {
            json.put("type", "Error")
            json.put("body", JSONObject().apply {
                put("message", this@toJson.message)
                put("originalSql", this@toJson.originalSql)
            }.toString())
        }

        is DatabaseExecuteSqlResponse.Insert -> {
            json.put("type", "Insert")
            json.put("body", JSONObject().apply {
                put("insertedId", this@toJson.insertedId)
            }.toString())
        }

        DatabaseExecuteSqlResponse.RawSuccess -> {
            json.put("type", "RawSuccess")
            json.put("body", JSONObject().toString())
        }

        is DatabaseExecuteSqlResponse.Select -> {
            json.put("type", "Select")
            json.put("body", JSONObject().apply {
                // Add columns
                put("columns", JSONArray().apply {
                    columns.forEach { put(it) }
                })
                put("values", JSONArray(values.map { row ->
                    JSONArray().also { rowArray ->
                        row.forEach { item ->
                            rowArray.put(item) // null values are handled correctly by JSONObject/JSONArray
                        }
                    }
                }))
            }.toString())
        }

        is DatabaseExecuteSqlResponse.UpdateDelete -> {
            json.put("type", "UpdateDelete")
            json.put("body", JSONObject().apply {
                json.put("affectedCount", affectedCount)
            })
        }
    }
    return json
}