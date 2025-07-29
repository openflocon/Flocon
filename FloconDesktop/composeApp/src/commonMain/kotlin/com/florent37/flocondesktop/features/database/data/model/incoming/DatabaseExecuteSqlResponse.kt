package com.florent37.flocondesktop.features.database.data.model.incoming

import kotlinx.serialization.Serializable

@Serializable
sealed interface DatabaseExecuteSqlResponse {
    @Serializable
    data class Select(
        val columns: List<String>,
        val values: List<List<String?>>,
    ) : DatabaseExecuteSqlResponse

    @Serializable
    data class Insert(
        val insertedId: Long,
    ) : DatabaseExecuteSqlResponse

    @Serializable
    data class UpdateDelete(
        val affectedCount: Int,
    ) : DatabaseExecuteSqlResponse

    // Pour les objets singuliers, vous pouvez utiliser @Serializable object
    @Serializable
    object RawSuccess : DatabaseExecuteSqlResponse

    @Serializable
    data class Error(
        val message: String,
        val originalSql: String,
    ) : DatabaseExecuteSqlResponse
}
