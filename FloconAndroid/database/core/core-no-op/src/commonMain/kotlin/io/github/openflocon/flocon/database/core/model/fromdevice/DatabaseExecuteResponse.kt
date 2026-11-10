package io.github.openflocon.flocon.database.core.model.fromdevice

sealed interface DatabaseExecuteResponse

sealed interface DatabaseExecuteSqlResponse : DatabaseExecuteResponse {

    class Select(
        val columns: List<String>,
        val values: List<List<String?>>
    ) : DatabaseExecuteSqlResponse

    class Insert(
        val insertedId: Long
    ) : DatabaseExecuteSqlResponse

    class UpdateDelete(
        val affectedCount: Int
    ) : DatabaseExecuteSqlResponse

    object RawSuccess : DatabaseExecuteSqlResponse

    class Error(
        val message: String = "",
        val originalSql: String = "",
    ) : DatabaseExecuteSqlResponse
}
