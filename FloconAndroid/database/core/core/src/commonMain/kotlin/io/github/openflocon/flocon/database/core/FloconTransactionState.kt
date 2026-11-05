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

    // Room 2 logs every nested beginTransaction and endTransaction, only the last end closes the transaction
    private var openBegins = 0
    private var startedBySavepoint = false

    /**
     * @return the id of the transaction the [sql] belongs to, null when it runs outside of a transaction
     */
    fun onQuery(sql: String): String? {
        val statement = sql.trimStart().uppercase()
        return when {
            statement.startsWith("BEGIN") -> {
                // a nested begin (Room 2) is still the same transaction
                val id = transactionId ?: start(bySavepoint = false)
                openBegins++
                id
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

            // ROLLBACK TO SAVEPOINT x, or ROLLBACK TRANSACTION TO SAVEPOINT x (Room 3): the transaction goes on
            statement.startsWith("ROLLBACK") && " TO " in statement -> transactionId

            statement.startsWith("COMMIT") || statement.startsWith("END") -> {
                val id = transactionId
                if (openBegins > 1) openBegins-- else reset()
                id
            }

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
        openBegins = 0
        startedBySavepoint = bySavepoint
        return id
    }

    private fun reset() {
        transactionId = null
        openSavepoints = 0
        openBegins = 0
        startedBySavepoint = false
    }
}
