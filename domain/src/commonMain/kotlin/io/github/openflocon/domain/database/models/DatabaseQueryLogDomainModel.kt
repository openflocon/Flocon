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
    /** The transaction nested in [transactionId] (a savepoint, a nested begin) the query runs in. Null in the outermost transaction. */
    val nestedTransactionId: String? = null,
    /** The transaction that holds the nested one: another nested transaction, or [transactionId]. Null without nested transaction. */
    val parentTransactionId: String? = null,
    /** 0 in the outermost transaction, 1 in a transaction nested in it, and so on. */
    val transactionDepth: Int = 0,
    /** Local id of the log, 0 until it is saved. */
    val id: Int = 0,
)

fun DatabaseQueryLogDomainModel.toFullSql(): String = injectSqlArgs(sqlQuery, bindArgs)
