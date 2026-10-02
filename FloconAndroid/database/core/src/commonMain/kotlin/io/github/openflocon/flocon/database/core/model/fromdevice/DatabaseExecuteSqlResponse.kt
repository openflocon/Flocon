package io.github.openflocon.flocon.database.core.model.fromdevice

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface DatabaseExecuteSqlResponse : DatabaseExecuteResponse {

    @Serializable
    @SerialName("SELECT")
    // Case for successful SELECT queries
    class Select(
        val columns: List<String>,
        val values: List<List<String?>>
    ) : DatabaseExecuteSqlResponse

    // Case for successful INSERT queries
    @Serializable
    @SerialName("INSERT")
    class Insert(
        val insertedId: Long
    ) : DatabaseExecuteSqlResponse

    // Case for successful UPDATE or DELETE queries
    @Serializable
    @SerialName("UPDATE_DELETE")
    class UpdateDelete(
        val affectedCount: Int
    ) : DatabaseExecuteSqlResponse

    // Case for successful "raw" queries (CREATE TABLE, DROP TABLE, etc.)
    @Serializable
    @SerialName("RAW_SUCCESS")
    object RawSuccess : DatabaseExecuteSqlResponse

    // Case for an SQL execution error
    @Serializable
    @SerialName("ERROR")
    class Error(
        val message: String = "",       // Detailed error message
        val originalSql: String = "",   // SQL query that caused the error (optional)
    ) : DatabaseExecuteSqlResponse
}
