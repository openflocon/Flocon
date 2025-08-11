package io.github.openflocon.flocondesktop.features.database.data.model.incoming

import kotlinx.serialization.Serializable

@Serializable
sealed interface DatabaseExecuteSqlResponseDataModel {
    @Serializable
    data class Select(
        val columns: List<String>,
        val values: List<List<String?>>,
    ) : DatabaseExecuteSqlResponseDataModel

    @Serializable
    data class Insert(
        val insertedId: Long,
    ) : DatabaseExecuteSqlResponseDataModel

    @Serializable
    data class UpdateDelete(
        val affectedCount: Int,
    ) : DatabaseExecuteSqlResponseDataModel

    // Pour les objets singuliers, vous pouvez utiliser @Serializable object
    @Serializable
    object RawSuccess : DatabaseExecuteSqlResponseDataModel

    @Serializable
    data class Error(
        val message: String,
        val originalSql: String,
    ) : DatabaseExecuteSqlResponseDataModel
}
