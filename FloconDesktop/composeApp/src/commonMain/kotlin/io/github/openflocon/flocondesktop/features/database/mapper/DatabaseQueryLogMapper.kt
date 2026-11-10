package io.github.openflocon.flocondesktop.features.database.mapper

import io.github.openflocon.domain.database.models.DatabaseQueryLogDomainModel
import io.github.openflocon.flocondesktop.common.ui.TransactionFolding
import io.github.openflocon.flocondesktop.features.database.model.DatabaseQueryLogEntryUiModel
import io.github.openflocon.flocondesktop.features.database.model.DatabaseQueryLogEntryUiModel.Transaction.Status
import io.github.openflocon.flocondesktop.features.database.model.DatabaseQueryUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Groups the logs of a page into rows: the logs sharing a transaction id form one row, the other ones are a row each.
 * The rows keep the order in which their first log appears.
 *
 * @param logs the logs of the page, oldest first
 * @param showTransactions false to hide the BEGIN / COMMIT statements, the group keeps its status
 */
fun toQueryLogEntries(
    logs: List<DatabaseQueryLogDomainModel>,
    showTransactions: Boolean,
    folding: TransactionFolding,
    currentAppInstance: Long?,
): ImmutableList<DatabaseQueryLogEntryUiModel> {
    val groups = LinkedHashMap<String, MutableList<DatabaseQueryLogDomainModel>>()
    logs.forEach { log -> groups.getOrPut(log.transactionId ?: "query-${log.id}") { mutableListOf() } += log }

    return groups.values.mapNotNull { group ->
        val transactionId = group.first().transactionId
        if (transactionId == null) {
            DatabaseQueryLogEntryUiModel.Query(group.single().toUi(currentAppInstance))
        } else {
            val items = transactionItems(group, showTransactions, folding, currentAppInstance)
            val onlyItem = items.singleOrNull() as? DatabaseQueryLogEntryUiModel.Transaction.Item.Query
            if (items.isEmpty()) {
                // nothing left to show once the statements of the transaction are hidden
                null
            } else if (onlyItem != null && !showTransactions) {
                // a transaction of one query is not worth a group
                DatabaseQueryLogEntryUiModel.Query(onlyItem.log)
            } else {
                DatabaseQueryLogEntryUiModel.Transaction(
                    id = transactionId,
                    dateFormatted = formatTime(group.first().timestamp),
                    durationLabel = "${group.last().timestamp - group.first().timestamp} ms",
                    queryCount = group.count { !it.isTransaction },
                    status = transactionStatus(group.map { it.sqlQuery }),
                    isExpanded = folding.isExpanded(transactionId),
                    isFromOldSession = group.first().appInstance != currentAppInstance,
                    items = items.toImmutableList(),
                )
            }
        }
    }.toImmutableList()
}

/**
 * What an expanded transaction shows: its queries in order, each transaction nested in it with a line of its own before its
 * queries. The queries of a folded nested transaction (or held by one) are left out, and so are the nested transactions
 * of a single query: that query is shown like the others.
 */
private fun transactionItems(
    group: List<DatabaseQueryLogDomainModel>,
    showTransactions: Boolean,
    folding: TransactionFolding,
    currentAppInstance: Long?,
): List<DatabaseQueryLogEntryUiModel.Transaction.Item> {
    // the nested transactions of the group: who holds them and what they ran
    val parents = HashMap<String, String?>()
    val logsOf = HashMap<String, MutableList<DatabaseQueryLogDomainModel>>()
    group.forEach { log ->
        val nestedId = log.nestedTransactionId ?: return@forEach
        parents.getOrPut(nestedId) { log.parentTransactionId?.takeIf { it != log.transactionId } }
        logsOf.getOrPut(nestedId) { mutableListOf() } += log
    }
    // the logs of a nested transaction and of the ones nested in it
    val subtrees = HashMap<String, List<DatabaseQueryLogDomainModel>>()
    fun subtree(id: String): List<DatabaseQueryLogDomainModel> = subtrees.getOrPut(id) {
        logsOf.entries
            .filter { (nestedId, _) -> generateSequence(nestedId) { parents[it] }.any { it == id } }
            .flatMap { it.value }
            .sortedBy { it.id }
    }
    // a nested transaction is worth a line when it ran more than one query, or when its statements are shown
    val headers = HashMap<String, Boolean>()
    fun hasHeader(id: String): Boolean = headers.getOrPut(id) { showTransactions || subtree(id).count { !it.isTransaction } > 1 }

    // the nested transactions that have a line and hold a log, from the outermost
    fun headersOf(nestedId: String?): List<String> = generateSequence(nestedId) { parents[it] }
        .toList()
        .reversed()
        .filter { hasHeader(it) }

    val items = mutableListOf<DatabaseQueryLogEntryUiModel.Transaction.Item>()
    val shown = HashSet<String>()
    group.forEach { log ->
        val holders = headersOf(log.nestedTransactionId)
        // the line of each nested transaction that holds the log, the first time one of its logs comes
        holders.forEachIndexed { index, id ->
            if (shown.add(id) && holders.take(index).all { folding.isExpanded(it) }) {
                val logs = subtree(id)
                items += DatabaseQueryLogEntryUiModel.Transaction.Item.Nested(
                    id = id,
                    durationLabel = "${logs.last().timestamp - logs.first().timestamp} ms",
                    queryCount = logs.count { !it.isTransaction },
                    status = nestedStatus(logsOf[id].orEmpty().map { it.sqlQuery }),
                    isExpanded = folding.isExpanded(id),
                    depth = index,
                )
            }
        }
        if ((showTransactions || !log.isTransaction) && holders.all { folding.isExpanded(it) }) {
            items += DatabaseQueryLogEntryUiModel.Transaction.Item.Query(log.toUi(currentAppInstance), depth = holders.size)
        }
    }
    return items
}

/**
 * @param sqls the statements the nested transaction ran itself, in the order they were run
 */
internal fun nestedStatus(sqls: List<String>): Status {
    val statements = sqls.map { it.trim().uppercase() }
    return when {
        statements.any { it.startsWith("ROLLBACK") } -> Status.RolledBack
        statements.any { it.startsWith("RELEASE") || it.startsWith("COMMIT") || it.startsWith("END") } -> Status.Committed
        else -> Status.Open
    }
}

/**
 * @param sqls the statements of the transaction, in the order they were run
 */
internal fun transactionStatus(sqls: List<String>): Status {
    val statements = sqls.map { it.trim().uppercase() }
    return when {
        statements.any { it.startsWith("ROLLBACK") && " TO " !in it } -> Status.RolledBack

        statements.any { it.startsWith("COMMIT") } -> Status.Committed

        // Room 2 ends its transactions with END TRANSACTION, and marks the successful ones just before
        statements.any { it.startsWith("END") } -> if (statements.any { it == "TRANSACTION SUCCESSFUL" }) Status.Committed else Status.RolledBack

        // a transaction started with a savepoint is committed when this savepoint is released
        statements.first().startsWith("SAVEPOINT") && statements.last().startsWith("RELEASE") -> Status.Committed

        else -> Status.Open
    }
}

fun DatabaseQueryLogDomainModel.toUi(currentAppInstance: Long?) = DatabaseQueryUiModel(
    id = id,
    sqlQuery = sqlQuery,
    bindArgs = bindArgs,
    dateFormatted = formatTime(timestamp),
    isTransaction = isTransaction,
    isFromOldSession = currentAppInstance != appInstance,
    type = findType(this),
)

private fun formatTime(timestamp: Long): String = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(timestamp))

private fun findType(model: DatabaseQueryLogDomainModel): DatabaseQueryUiModel.Type? = when {
    model.isTransaction -> DatabaseQueryUiModel.Type.Transaction
    model.sqlQuery.startsWith("SELECT") -> DatabaseQueryUiModel.Type.Select
    model.sqlQuery.startsWith("INSERT") -> DatabaseQueryUiModel.Type.Insert
    model.sqlQuery.startsWith("UPDATE") -> DatabaseQueryUiModel.Type.Update
    model.sqlQuery.startsWith("DELETE") -> DatabaseQueryUiModel.Type.Delete
    else -> null
}
