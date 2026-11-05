package io.github.openflocon.flocondesktop.features.database.model

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * A row of the query logs: a query outside of a transaction, or all the queries of one transaction.
 */
@Immutable
sealed interface DatabaseQueryLogEntryUiModel {

    /** Stable key of the row. */
    val key: String

    @Immutable
    data class Query(
        val log: DatabaseQueryUiModel,
    ) : DatabaseQueryLogEntryUiModel {
        override val key: String = "query-${log.id}"
    }

    @Immutable
    data class Transaction(
        val id: String,
        val dateFormatted: String,
        val durationLabel: String,
        val queryCount: Int,
        val status: Status,
        val isExpanded: Boolean,
        val isFromOldSession: Boolean,
        val queries: ImmutableList<DatabaseQueryUiModel>,
    ) : DatabaseQueryLogEntryUiModel {
        override val key: String = "transaction-$id"

        enum class Status {
            Committed,
            RolledBack,
            Open,
        }
    }
}

fun previewTransactionEntry(
    status: DatabaseQueryLogEntryUiModel.Transaction.Status = DatabaseQueryLogEntryUiModel.Transaction.Status.Committed,
    isExpanded: Boolean = true,
) = DatabaseQueryLogEntryUiModel.Transaction(
    id = "tx-1",
    dateFormatted = "12:03:41",
    durationLabel = "12 ms",
    queryCount = 2,
    status = status,
    isExpanded = isExpanded,
    isFromOldSession = false,
    queries = persistentListOf(
        previewDatabaseQuery(id = 1, sqlQuery = "INSERT INTO dog (name, age) VALUES (?, ?)", type = DatabaseQueryUiModel.Type.Insert),
        previewDatabaseQuery(id = 2, sqlQuery = "UPDATE owner SET dogs = dogs + 1", type = DatabaseQueryUiModel.Type.Update),
    ),
)

fun previewDatabaseQuery(
    id: Int = 0,
    sqlQuery: String = "SELECT * FROM dog",
    type: DatabaseQueryUiModel.Type? = DatabaseQueryUiModel.Type.Select,
) = DatabaseQueryUiModel(
    id = id,
    sqlQuery = sqlQuery,
    bindArgs = null,
    dateFormatted = "12:03:41",
    isTransaction = false,
    isFromOldSession = false,
    type = type,
)
