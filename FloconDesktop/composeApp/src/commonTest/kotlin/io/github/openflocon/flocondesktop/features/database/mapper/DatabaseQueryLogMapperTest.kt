package io.github.openflocon.flocondesktop.features.database.mapper

import io.github.openflocon.domain.database.models.DatabaseQueryLogDomainModel
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
    )

    private fun entries(
        logs: List<DatabaseQueryLogDomainModel>,
        showTransactions: Boolean = true,
        expanded: Set<String> = emptySet(),
    ) = toQueryLogEntries(logs, showTransactions, expanded, currentAppInstance = 1)

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
                log("ROLLBACK", 45, "tx", isTransaction = true),
            ),
            showTransactions = false,
        )

        val transaction = assertIs<DatabaseQueryLogEntryUiModel.Transaction>(result.single())
        assertEquals(listOf("INSERT INTO dog VALUES (1)"), transaction.queries.map { it.sqlQuery })
        assertEquals(Status.RolledBack, transaction.status)
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
    fun `a transaction is folded unless it is expanded`() {
        val logs = listOf(log("BEGIN", 20, "tx", isTransaction = true), log("SELECT 1", 21, "tx"))

        assertEquals(false, assertIs<DatabaseQueryLogEntryUiModel.Transaction>(entries(logs).single()).isExpanded)
        assertEquals(true, assertIs<DatabaseQueryLogEntryUiModel.Transaction>(entries(logs, expanded = setOf("tx")).single()).isExpanded)
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
}
