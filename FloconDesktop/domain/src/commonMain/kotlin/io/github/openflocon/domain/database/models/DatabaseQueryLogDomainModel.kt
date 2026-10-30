package io.github.openflocon.domain.database.models

import io.github.openflocon.domain.database.utils.injectSqlArgs

data class DatabaseQueryLogDomainModel(
    val dbId: String,
    val sqlQuery: String,
    val bindArgs: List<String>?,
    val timestamp: Long,
    val isTransaction: Boolean,
    val appInstance: Long,
)

fun DatabaseQueryLogDomainModel.toFullSql(): String = injectSqlArgs(sqlQuery, bindArgs)
