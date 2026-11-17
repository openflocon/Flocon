package io.github.openflocon.flocon.database.core.model.fromdevice.sql

import kotlinx.serialization.Serializable

@Serializable
internal data class DatabaseQueryLogModel(
    val dbId: String,
    val sqlQuery: String,
    val bindArgs: List<String>?,
    val timestamp: Long,
    val transactionId: String? = null,
    // the transaction nested in the one above (savepoint, nested begin) the query runs in, and the transaction that holds it
    val nestedTransactionId: String? = null,
    val parentTransactionId: String? = null,
    // 1 in a nested transaction, 2 in one nested in it... absent in the outermost one
    val transactionDepth: Int? = null,
)
