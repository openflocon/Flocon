package io.github.openflocon.flocon.plugins.database.model.fromdevice

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.add
import kotlinx.serialization.json.addJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject

internal sealed interface DatabaseExecuteSqlResponse {

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

internal fun DatabaseExecuteSqlResponse.toJson(): JsonObject {
    return buildJsonObject {
        when (this@toJson) {
            is DatabaseExecuteSqlResponse.Error -> {
                put("type", "Error")
                putJsonObject("body") {
                    put("message", message)
                    put("originalSql", originalSql)
                }
            }

            is DatabaseExecuteSqlResponse.Insert -> {
                put("type", "Insert")
                putJsonObject("body") {
                    put("insertedId", insertedId)
                }
            }

            DatabaseExecuteSqlResponse.RawSuccess -> {
                put("type", "RawSuccess")
                putJsonObject("body") {
                    // empty
                }
            }

            is DatabaseExecuteSqlResponse.Select -> {
                put("type", "Select")
                putJsonObject("body") {
                    putJsonArray("columns") {
                        columns.forEach { add(it) }
                    }
                    putJsonArray("values") {
                        values.forEach { row ->
                            addJsonArray {
                                row.forEach { item -> add(item) }
                            }
                        }
                    }
                }
            }

            is DatabaseExecuteSqlResponse.UpdateDelete -> {
                put("type", "UpdateDelete")
                putJsonObject("body") {
                    put("affectedCount", affectedCount)
                }
            }
        }
    }
}

