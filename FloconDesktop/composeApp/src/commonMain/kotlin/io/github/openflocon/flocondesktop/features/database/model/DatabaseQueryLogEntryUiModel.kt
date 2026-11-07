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
        // what the transaction shows when it is expanded, in order: its queries, with the transactions nested in it
        val items: ImmutableList<Item>,
    ) : DatabaseQueryLogEntryUiModel {
        override val key: String = "transaction-$id"

        // the queries of the transaction, the ones of its nested transactions included
        val queries: List<DatabaseQueryUiModel> get() = items.filterIsInstance<Item.Query>().map { it.log }

        /**
         * A line inside an expanded transaction. [depth] is the number of transactions that hold it under the one that
         * shows it, to indent it.
         */
        @Immutable
        sealed interface Item {
            val key: String
            val depth: Int

            @Immutable
            data class Query(
                val log: DatabaseQueryUiModel,
                override val depth: Int,
            ) : Item {
                override val key: String = "query-${log.id}"
            }

            /** A transaction nested in the one above (a savepoint), its queries follow while it is expanded. */
            @Immutable
            data class Nested(
                val id: String,
                val durationLabel: String,
                val queryCount: Int,
                val status: Status,
                val isExpanded: Boolean,
                override val depth: Int,
            ) : Item {
                override val key: String = "nested-$id"
            }
        }

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
    items = persistentListOf(
        DatabaseQueryLogEntryUiModel.Transaction.Item.Query(
            previewDatabaseQuery(id = 1, sqlQuery = "INSERT INTO dog (name, age) VALUES (?, ?)", type = DatabaseQueryUiModel.Type.Insert),
            depth = 0,
        ),
        DatabaseQueryLogEntryUiModel.Transaction.Item.Nested(
            id = "nested-1",
            durationLabel = "3 ms",
            queryCount = 2,
            status = DatabaseQueryLogEntryUiModel.Transaction.Status.Committed,
            isExpanded = true,
            depth = 0,
        ),
        DatabaseQueryLogEntryUiModel.Transaction.Item.Query(
            previewDatabaseQuery(id = 2, sqlQuery = "UPDATE owner SET dogs = dogs + 1", type = DatabaseQueryUiModel.Type.Update),
            depth = 1,
        ),
        DatabaseQueryLogEntryUiModel.Transaction.Item.Query(
            previewDatabaseQuery(id = 3, sqlQuery = "INSERT INTO owner_dog VALUES (1, 1)", type = DatabaseQueryUiModel.Type.Insert),
            depth = 1,
        ),
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
