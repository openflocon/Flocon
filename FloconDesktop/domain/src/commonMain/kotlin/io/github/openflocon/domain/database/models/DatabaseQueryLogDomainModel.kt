package io.github.openflocon.domain.database.models

import io.github.openflocon.domain.database.utils.injectSqlArgs

data class DatabaseQueryLogDomainModel(
    val dbId: String,
    /** Display name of the database, only for the screens: the database is always identified by [dbId]. */
    val dbName: String,
    val sqlQuery: String,
    val bindArgs: List<String>?,
    val timestamp: Long,
    val isTransaction: Boolean,
    val appInstance: Long,
    /** The queries of a transaction share the same id, BEGIN and COMMIT included. Null outside of a transaction. */
    val transactionId: String? = null,
    /** Local id of the log, 0 until it is saved. */
    val id: Int = 0,
)

fun DatabaseQueryLogDomainModel.toFullSql(): String = injectSqlArgs(sqlQuery, bindArgs)
