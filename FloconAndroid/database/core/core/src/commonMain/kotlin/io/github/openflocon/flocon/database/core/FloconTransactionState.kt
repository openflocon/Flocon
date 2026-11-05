package io.github.openflocon.flocon.database.core

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Follows the transaction of one execution scope (a connection, or a calling thread) from the SQL it runs, to give the
 * same id to every query of a transaction, `BEGIN` and `COMMIT` included.
 *
 * Not thread safe: use one instance per scope.
 */
class FloconTransactionState {

    private var transactionId: String? = null
    private var openSavepoints = 0
    private var startedBySavepoint = false

    /**
     * @return the id of the transaction the [sql] belongs to, null when it runs outside of a transaction
     */
    fun onQuery(sql: String): String? {
        val statement = sql.trimStart().uppercase()
        return when {
            statement.startsWith("BEGIN") -> {
                // nested begins are not sent to SQLite, but stay safe: it is still the same transaction
                transactionId ?: start(bySavepoint = false)
            }

            statement.startsWith("SAVEPOINT") -> {
                val id = transactionId ?: start(bySavepoint = true)
                openSavepoints++
                id
            }

            statement.startsWith("RELEASE") -> {
                val id = transactionId
                if (openSavepoints > 0) openSavepoints--
                if (openSavepoints == 0 && startedBySavepoint) reset()
                id
            }

            statement.startsWith("ROLLBACK TO") -> transactionId

            statement.startsWith("COMMIT") ||
                statement.startsWith("END") ||
                statement.startsWith("ROLLBACK") -> {
                val id = transactionId
                reset()
                id
            }

            else -> transactionId
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun start(bySavepoint: Boolean): String {
        val id = Uuid.random().toString()
        transactionId = id
        openSavepoints = 0
        startedBySavepoint = bySavepoint
        return id
    }

    private fun reset() {
        transactionId = null
        openSavepoints = 0
        startedBySavepoint = false
    }
}
