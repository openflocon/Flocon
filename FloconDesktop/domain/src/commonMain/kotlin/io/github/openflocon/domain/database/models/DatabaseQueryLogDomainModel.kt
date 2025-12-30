package io.github.openflocon.domain.database.models

import io.github.openflocon.domain.database.utils.injectSqlArgs

data class DatabaseQueryLogDomainModel(
    val dbName: String,
    val sqlQuery: String,
    val bindArgs: List<String>?,
    val timestamp: Long,
    val isTransaction: Boolean,
    val appInstance: Long,
)

fun DatabaseQueryLogDomainModel.toFullSql(): String {
    return injectSqlArgs(sqlQuery, bindArgs)
}