package io.github.openflocon.domain.database.models

data class DatabaseFavoriteQueryDomainModel(
    val id: Long,
    val databaseId: String,
    val title: String,
    val query: String,
)