package io.github.openflocon.flocon.database.core.model

import io.github.openflocon.flocon.database.core.model.fromdevice.DatabaseExecuteResponse
import io.github.openflocon.flocon.database.core.model.fromdevice.DatabaseExecuteSqlResponse

interface FloconDatabaseModel {
    val id: String
    val displayName: String

    suspend fun executeQuery(query: String): DatabaseExecuteResponse

}

data class FloconFileDatabaseModel(
    override val id: String,
    override val displayName: String,
    val absolutePath: String
) : FloconDatabaseModel {

    override suspend fun executeQuery(query: String): DatabaseExecuteResponse {
        return openDbAndExecuteQuery(absolutePath, query)
    }

}

expect fun openDbAndExecuteQuery(
    path: String,
    query: String
): DatabaseExecuteSqlResponse