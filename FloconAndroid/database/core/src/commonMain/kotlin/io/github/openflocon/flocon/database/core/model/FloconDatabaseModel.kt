package io.github.openflocon.flocon.database.core.model

import io.github.openflocon.flocon.database.core.model.fromdevice.DatabaseExecuteResponse

interface FloconDatabaseModel {
    val displayName: String

    suspend fun executeQuery(query: String): DatabaseExecuteResponse

}

data class FloconFileDatabaseModel(
    override val displayName: String,
    val absolutePath: String
) : FloconDatabaseModel {
    override suspend fun executeQuery(query: String): DatabaseExecuteResponse {
        TODO("Not yet implemented")
    }
}
