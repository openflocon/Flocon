package io.github.openflocon.flocon.plugins.database.model.fromdevice

import io.github.openflocon.flocon.core.FloconEncoder
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.put

@Serializable
internal sealed interface DatabaseExecuteSqlResponse {

    @Serializable
    // Case for successful SELECT queries
    class Select(
        val columns: List<String>,
        val values: List<List<String?>>
    ) : DatabaseExecuteSqlResponse

    // Case for successful INSERT queries
    @Serializable
    class Insert(
        val insertedId: Long
    ) : DatabaseExecuteSqlResponse

    // Case for successful UPDATE or DELETE queries
    @Serializable
    class UpdateDelete(
        val affectedCount: Int
    ) : DatabaseExecuteSqlResponse

    // Case for successful "raw" queries (CREATE TABLE, DROP TABLE, etc.)
    @Serializable
    object RawSuccess : DatabaseExecuteSqlResponse

    // Case for an SQL execution error
    @Serializable
    class Error(
        val message: String,       // Detailed error message
        val originalSql: String,   // SQL query that caused the error (optional)
    ) : DatabaseExecuteSqlResponse
}

internal fun DatabaseExecuteSqlResponse.toJson(): String {
    val jsonEncoder = FloconEncoder.json
    val thisAsJson = jsonEncoder.encodeToJsonElement(this)

    val type = when (this) {
        is DatabaseExecuteSqlResponse.Error -> "Error"
        is DatabaseExecuteSqlResponse.Insert -> "Insert"
        DatabaseExecuteSqlResponse.RawSuccess -> "RawSuccess"
        is DatabaseExecuteSqlResponse.Select -> "Select"
        is DatabaseExecuteSqlResponse.UpdateDelete -> "UpdateDelete"
    }

    return buildJsonObject {
        put("type", type)
        put("body", thisAsJson.toString()) // warning : the desktop is waiting for a string representation of the json here
    }.toString()
}

