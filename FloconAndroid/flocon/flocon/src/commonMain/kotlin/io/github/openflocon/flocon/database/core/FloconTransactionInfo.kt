package io.github.openflocon.flocon.database.core

/**
 * The transaction a query belongs to. A query of a transaction nested in another one (a savepoint, or a nested begin of
 * Room 2) has the id of the outermost transaction like all the others, and tells its own nested transaction.
 *
 * @property transactionId the id of the outermost transaction, the same for every query of it
 * @property nestedTransactionId the id of the innermost nested transaction the query runs in, null when it runs in the outermost one
 * @property parentTransactionId the id of the transaction that holds the nested one: another nested transaction, or the outermost one. Null without nested transaction
 * @property depth 0 in the outermost transaction, 1 in a transaction nested in it, and so on
 */
data class FloconTransactionInfo(
    val transactionId: String,
    val nestedTransactionId: String? = null,
    val parentTransactionId: String? = null,
    val depth: Int = 0,
)
