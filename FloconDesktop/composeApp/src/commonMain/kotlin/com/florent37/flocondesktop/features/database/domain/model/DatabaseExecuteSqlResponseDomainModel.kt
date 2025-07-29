package com.florent37.flocondesktop.features.database.domain.model

sealed interface DatabaseExecuteSqlResponseDomainModel {
    data class Select(
        val columns: List<String>,
        val values: List<List<String?>>,
    ) : DatabaseExecuteSqlResponseDomainModel

    data class Insert(
        val insertedId: Long,
    ) : DatabaseExecuteSqlResponseDomainModel

    data class UpdateDelete(
        val affectedCount: Int,
    ) : DatabaseExecuteSqlResponseDomainModel

    object RawSuccess : DatabaseExecuteSqlResponseDomainModel

    data class Error(
        val message: String,
        val originalSql: String,
    ) : DatabaseExecuteSqlResponseDomainModel
}
