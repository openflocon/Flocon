package com.flocon.data.remote.database.models

import kotlinx.serialization.Serializable

@Serializable
data class DatabaseQueryLogModel(
    val dbId: String,
    val sqlQuery: String,
    val bindArgs: List<String>?,
    val timestamp: Long,
    // the queries of a transaction share the same id, null with an older SDK or outside of a transaction
    val transactionId: String? = null,
    // the transaction nested in it (savepoint, nested begin) the query runs in, and the transaction that holds the nested one
    val nestedTransactionId: String? = null,
    val parentTransactionId: String? = null,
    // 1 in a nested transaction, 2 in one nested in it... absent in the outermost one
    val transactionDepth: Int? = null,
)
