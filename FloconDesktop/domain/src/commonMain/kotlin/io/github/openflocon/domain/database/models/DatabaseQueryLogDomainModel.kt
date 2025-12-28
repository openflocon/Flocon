package io.github.openflocon.domain.database.models

data class DatabaseQueryLogDomainModel(
    val dbName: String,
    val sqlQuery: String,
    val bindArgs: List<String>?,
    val timestamp: Long,
    val isTransaction: Boolean = false,
)
