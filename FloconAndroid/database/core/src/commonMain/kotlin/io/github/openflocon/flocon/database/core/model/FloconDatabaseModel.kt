package io.github.openflocon.flocon.database.core.model

interface FloconDatabaseModel {
    val displayName: String
}

interface FloconSqlDatabaseModel : FloconDatabaseModel {
    suspend fun executeSQL(query: String): io.github.openflocon.flocon.database.core.model.fromdevice.DatabaseExecuteSqlResponse
}

data class FloconFileDatabaseModel(
    override val displayName: String,
    val absolutePath: String
) : FloconDatabaseModel
