package io.github.openflocon.domain.database.models

data class DatabaseQueryLogDomainModel(
    val dbName: String,
    val path: String,
    val sqlQuery: String,
    val bindArgs: String?,
    val timestamp: Long
)
