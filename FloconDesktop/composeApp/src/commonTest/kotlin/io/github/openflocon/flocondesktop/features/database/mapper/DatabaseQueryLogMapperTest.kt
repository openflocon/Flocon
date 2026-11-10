package io.github.openflocon.flocondesktop.features.database.mapper

import io.github.openflocon.domain.database.models.DatabaseQueryLogDomainModel
import io.github.openflocon.flocondesktop.common.ui.TransactionFolding
import io.github.openflocon.flocondesktop.features.database.model.DatabaseQueryLogEntryUiModel
import io.github.openflocon.flocondesktop.features.database.model.DatabaseQueryLogEntryUiModel.Transaction.Status
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class DatabaseQueryLogMapperTest {

    private var nextId = 1

    private fun log(
        sql: String,
        timestamp: Long,
        transactionId: String? = null,
        isTransaction: Boolean = false,
        nested: String? = null,
        parent: String? = null,
        depth: Int = 0,
    ) = DatabaseQueryLogDomainModel(
        id = nextId++,
        dbId = "dogs",
        dbName = "dogs",
        sqlQuery = sql,
        bindArgs = null,
        timestamp = timestamp,
        isTransaction = isTransaction,
        appInstance = 1,
        transactionId = transactionId,
        nestedTransactionId = nested,
        parentTransactionId = parent,
        transactionDepth = depth,
    )

    private fun entries(
        logs: List<DatabaseQueryLogDomainModel>,
        showTransactions: Boolean = true,
        folding: TransactionFolding = TransactionFolding(),
    ) = toQueryLogEntries(logs, showTransactions, folding, currentAppInstance = 1)

    @Test
    fun `the logs of a transaction are one row, in the order of its first log`() {
        val result = entries(
            listOf(
                log("SELECT 1", 10),
                log("BEGIN IMMEDIATE TRANSACTION", 20, "tx", isTransaction = true),
                log("SELECT 2", 25),
                log("INSERT INTO dog VALUES (1)", 30, "tx"),
                log("COMMIT", 45, "tx", isTransaction = true),
            ),
        )

        assertEquals(3, result.size)
        assertIs<DatabaseQueryLogEntryUiModel.Query>(result[0])
        val transaction = assertIs<DatabaseQueryLogEntryUiModel.Transaction>(result[1])
        assertIs<DatabaseQueryLogEntryUiModel.Query>(result[2])
        assertEquals(1, transaction.queryCount)
        assertEquals("25 ms", transaction.durationLabel)
        assertEquals(Status.Committed, transaction.status)
        assertEquals(3, transaction.queries.size)
    }

    @Test
    fun `the statements of the transaction are hidden but the status stays`() {
        val result = entries(
            listOf(
                log("BEGIN", 20, "tx", isTransaction = true),
                log("INSERT INTO dog VALUES (1)", 30, "tx"),
                log("INSERT INTO dog VALUES (2)", 31, "tx"),
                log("ROLLBACK", 45, "tx", isTransaction = true),
            ),
            showTransactions = false,
        )

        val transaction = assertIs<DatabaseQueryLogEntryUiModel.Transaction>(result.single())
        assertEquals(listOf("INSERT INTO dog VALUES (1)", "INSERT INTO dog VALUES (2)"), transaction.queries.map { it.sqlQuery })
        assertEquals(Status.RolledBack, transaction.status)
    }

    @Test
    fun `a transaction of one query is a row like the others once its statements are hidden`() {
        val logs = listOf(
            log("BEGIN", 20, "tx", isTransaction = true),
            log("INSERT INTO dog VALUES (1)", 30, "tx"),
            log("END TRANSACTION", 45, "tx", isTransaction = true),
        )

        val hidden = entries(logs, showTransactions = false)
        assertEquals("INSERT INTO dog VALUES (1)", assertIs<DatabaseQueryLogEntryUiModel.Query>(hidden.single()).log.sqlQuery)

        // with its statements shown, they need the group
        assertIs<DatabaseQueryLogEntryUiModel.Transaction>(entries(logs, showTransactions = true).single())
    }

    @Test
    fun `a transaction with only statements is dropped when they are hidden`() {
        val result = entries(
            listOf(
                log("BEGIN", 20, "tx", isTransaction = true),
                log("COMMIT", 21, "tx", isTransaction = true),
            ),
            showTransactions = false,
        )

        assertTrue(result.isEmpty())
    }

    @Test
    fun `a transaction shows its queries unless it is folded`() {
        val logs = listOf(log("BEGIN", 20, "tx", isTransaction = true), log("SELECT 1", 21, "tx"))

        assertEquals(true, assertIs<DatabaseQueryLogEntryUiModel.Transaction>(entries(logs).single()).isExpanded)
        assertEquals(false, assertIs<DatabaseQueryLogEntryUiModel.Transaction>(entries(logs, folding = TransactionFolding(toggled = setOf("tx"))).single()).isExpanded)
    }

    @Test
    fun `with the setting off a transaction is folded unless the user unfolds it`() {
        val logs = listOf(log("BEGIN", 20, "tx", isTransaction = true), log("SELECT 1", 21, "tx"))
        val off = TransactionFolding(expandedByDefault = false)

        assertEquals(false, assertIs<DatabaseQueryLogEntryUiModel.Transaction>(entries(logs, folding = off).single()).isExpanded)
        assertEquals(true, assertIs<DatabaseQueryLogEntryUiModel.Transaction>(entries(logs, folding = off.copy(toggled = setOf("tx"))).single()).isExpanded)
    }

    @Test
    fun `the status follows the statements of the transaction`() {
        assertEquals(Status.Committed, transactionStatus(listOf("BEGIN", "INSERT", "COMMIT")))
        assertEquals(Status.RolledBack, transactionStatus(listOf("BEGIN", "INSERT", "ROLLBACK")))
        assertEquals(Status.Committed, transactionStatus(listOf("BEGIN EXCLUSIVE TRANSACTION", "INSERT", "TRANSACTION SUCCESSFUL", "END TRANSACTION")))
        assertEquals(Status.RolledBack, transactionStatus(listOf("BEGIN EXCLUSIVE TRANSACTION", "INSERT", "END TRANSACTION")))
        assertEquals(Status.Committed, transactionStatus(listOf("SAVEPOINT sp", "INSERT", "RELEASE sp")))
        assertEquals(Status.Open, transactionStatus(listOf("BEGIN", "INSERT")))
        assertEquals(Status.Open, transactionStatus(listOf("BEGIN", "ROLLBACK TO SAVEPOINT sp")))
    }

    // transaction { transaction { insertUsers() } transaction { insertPosts() insertComments() } }
    private fun sampleLogs() = listOf(
        log("BEGIN IMMEDIATE TRANSACTION", 10, "tx", isTransaction = true),
        log("SAVEPOINT 'a'", 11, "tx", isTransaction = true, nested = "a", parent = "tx", depth = 1),
        log("INSERT INTO user VALUES (1)", 12, "tx", nested = "a", parent = "tx", depth = 1),
        log("INSERT INTO user VALUES (2)", 13, "tx", nested = "a", parent = "tx", depth = 1),
        log("RELEASE SAVEPOINT 'a'", 14, "tx", isTransaction = true, nested = "a", parent = "tx", depth = 1),
        log("SAVEPOINT 'b'", 15, "tx", isTransaction = true, nested = "b", parent = "tx", depth = 1),
        log("INSERT INTO post VALUES (1)", 16, "tx", nested = "b", parent = "tx", depth = 1),
        log("INSERT INTO comment VALUES (1)", 17, "tx", nested = "b", parent = "tx", depth = 1),
        log("RELEASE SAVEPOINT 'b'", 18, "tx", isTransaction = true, nested = "b", parent = "tx", depth = 1),
        log("END TRANSACTION", 19, "tx", isTransaction = true),
    )

    private fun DatabaseQueryLogEntryUiModel.Transaction.Item.describe() = when (this) {
        is DatabaseQueryLogEntryUiModel.Transaction.Item.Query -> "query:${log.sqlQuery.substringAfter("INTO ").substringBefore(" ")}@$depth"
        is DatabaseQueryLogEntryUiModel.Transaction.Item.Nested -> "nested:$id@$depth(${if (isExpanded) "open" else "folded"},$queryCount)"
    }

    @Test
    fun `the transactions nested in a transaction have a line before their queries`() {
        val transaction = assertIs<DatabaseQueryLogEntryUiModel.Transaction>(entries(sampleLogs(), showTransactions = false).single())

        assertEquals(
            listOf("nested:a@0(open,2)", "query:user@1", "query:user@1", "nested:b@0(open,2)", "query:post@1", "query:comment@1"),
            transaction.items.map { it.describe() },
        )
        assertEquals(Status.Committed, (transaction.items[0] as DatabaseQueryLogEntryUiModel.Transaction.Item.Nested).status)
    }

    @Test
    fun `a folded nested transaction hides its queries but not the others`() {
        val folding = TransactionFolding(toggled = setOf("a"))
        val transaction = assertIs<DatabaseQueryLogEntryUiModel.Transaction>(entries(sampleLogs(), showTransactions = false, folding = folding).single())

        assertEquals(
            listOf("nested:a@0(folded,2)", "nested:b@0(open,2)", "query:post@1", "query:comment@1"),
            transaction.items.map { it.describe() },
        )
    }

    @Test
    fun `a nested transaction of one query has no line, its query is like the others`() {
        val logs = listOf(
            log("BEGIN IMMEDIATE TRANSACTION", 10, "tx", isTransaction = true),
            log("INSERT INTO dog VALUES (1)", 11, "tx"),
            log("SAVEPOINT 'a'", 12, "tx", isTransaction = true, nested = "a", parent = "tx", depth = 1),
            log("INSERT INTO user VALUES (1)", 13, "tx", nested = "a", parent = "tx", depth = 1),
            log("RELEASE SAVEPOINT 'a'", 14, "tx", isTransaction = true, nested = "a", parent = "tx", depth = 1),
            log("END TRANSACTION", 15, "tx", isTransaction = true),
        )

        val transaction = assertIs<DatabaseQueryLogEntryUiModel.Transaction>(entries(logs, showTransactions = false).single())

        assertEquals(listOf("query:dog@0", "query:user@0"), transaction.items.map { it.describe() })
    }

    @Test
    fun `a nested transaction in a nested transaction is indented and hidden with its parent`() {
        val logs = listOf(
            log("BEGIN IMMEDIATE TRANSACTION", 10, "tx", isTransaction = true),
            log("SAVEPOINT 'a'", 11, "tx", isTransaction = true, nested = "a", parent = "tx", depth = 1),
            log("INSERT INTO dog VALUES (1)", 12, "tx", nested = "a", parent = "tx", depth = 1),
            log("SAVEPOINT 'b'", 13, "tx", isTransaction = true, nested = "b", parent = "a", depth = 2),
            log("INSERT INTO cat VALUES (1)", 14, "tx", nested = "b", parent = "a", depth = 2),
            log("INSERT INTO cat VALUES (2)", 15, "tx", nested = "b", parent = "a", depth = 2),
            log("RELEASE SAVEPOINT 'b'", 16, "tx", isTransaction = true, nested = "b", parent = "a", depth = 2),
            log("RELEASE SAVEPOINT 'a'", 17, "tx", isTransaction = true, nested = "a", parent = "tx", depth = 1),
            log("END TRANSACTION", 18, "tx", isTransaction = true),
        )

        val open = assertIs<DatabaseQueryLogEntryUiModel.Transaction>(entries(logs, showTransactions = false).single())
        assertEquals(
            listOf("nested:a@0(open,3)", "query:dog@1", "nested:b@1(open,2)", "query:cat@2", "query:cat@2"),
            open.items.map { it.describe() },
        )

        val folded = assertIs<DatabaseQueryLogEntryUiModel.Transaction>(
            entries(logs, showTransactions = false, folding = TransactionFolding(toggled = setOf("a"))).single(),
        )
        assertEquals(listOf("nested:a@0(folded,3)"), folded.items.map { it.describe() })
    }

    @Test
    fun `the statements of the nested transactions are shown with the transactions`() {
        val transaction = assertIs<DatabaseQueryLogEntryUiModel.Transaction>(entries(sampleLogs(), showTransactions = true).single())

        assertEquals(10, transaction.queries.size)
    }

    @Test
    fun `the status of a nested transaction follows its own statements`() {
        assertEquals(Status.Committed, nestedStatus(listOf("SAVEPOINT 'a'", "INSERT", "RELEASE SAVEPOINT 'a'")))
        assertEquals(Status.RolledBack, nestedStatus(listOf("SAVEPOINT 'a'", "INSERT", "ROLLBACK TRANSACTION TO SAVEPOINT 'a'")))
        assertEquals(Status.Open, nestedStatus(listOf("SAVEPOINT 'a'", "INSERT")))
    }
}
