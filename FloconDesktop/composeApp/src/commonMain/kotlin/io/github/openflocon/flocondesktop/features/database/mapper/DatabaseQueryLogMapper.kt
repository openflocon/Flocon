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
            val queries = group
                .filter { showTransactions || !it.isTransaction }
                .map { it.toUi(currentAppInstance) }
            if (queries.isEmpty()) {
                // nothing left to show once the statements of the transaction are hidden
                null
            } else if (queries.size == 1 && !showTransactions) {
                // a transaction of one query is not worth a group
                DatabaseQueryLogEntryUiModel.Query(queries.single())
            } else {
                DatabaseQueryLogEntryUiModel.Transaction(
                    id = transactionId,
                    dateFormatted = formatTime(group.first().timestamp),
                    durationLabel = "${group.last().timestamp - group.first().timestamp} ms",
                    queryCount = group.count { !it.isTransaction },
                    status = transactionStatus(group.map { it.sqlQuery }),
                    isExpanded = folding.isExpanded(transactionId),
                    isFromOldSession = group.first().appInstance != currentAppInstance,
                    queries = queries.toImmutableList(),
                )
            }
        }
    }.toImmutableList()
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
