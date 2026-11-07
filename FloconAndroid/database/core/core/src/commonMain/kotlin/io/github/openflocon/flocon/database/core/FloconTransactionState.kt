package io.github.openflocon.flocon.database.core

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Follows the transaction of one execution scope (a connection, or a calling thread) from the SQL it runs, to give the
 * same id to every query of a transaction, `BEGIN` and `COMMIT` included, and to tell the transactions nested in it
 * (savepoints, nested begins of Room 2).
 *
 * Not thread safe: use one instance per scope.
 */
class FloconTransactionState {

    // a transaction nested in the outermost one: a savepoint (named), or a nested begin of Room 2 (no name)
    private class Nested(val id: String, val name: String?)

    private var transactionId: String? = null

    // the name of the savepoint that started the outermost transaction, null when it was started by a begin
    private var rootSavepoint: String? = null
    private val nested = ArrayList<Nested>()

    /**
     * @return the id of the outermost transaction the [sql] belongs to, null when it runs outside of a transaction
     */
    fun onQuery(sql: String): String? = track(sql)?.transactionId

    /**
     * @return the transaction the [sql] belongs to, null when it runs outside of a transaction
     */
    fun track(sql: String): FloconTransactionInfo? {
        val statement = sql.trimStart().uppercase()
        return when {
            statement.startsWith("BEGIN") -> if (transactionId == null) {
                start(savepoint = null)
            } else {
                // a nested begin (Room 2) is a transaction nested in the current one
                push(name = null)
            }

            statement.startsWith("SAVEPOINT") -> if (transactionId == null) {
                start(savepoint = savepointName(sql))
            } else {
                push(name = savepointName(sql))
            }

            statement.startsWith("RELEASE") -> release(savepointName(sql), endsRoot = true)

            // ROLLBACK TO SAVEPOINT x, or ROLLBACK TRANSACTION TO SAVEPOINT x (Room 3): the savepoint is over
            statement.startsWith("ROLLBACK") && " TO " in statement -> release(savepointName(sql), endsRoot = false)

            statement.startsWith("COMMIT") || statement.startsWith("END") -> {
                val info = current()
                val top = nested.lastOrNull()
                if (top != null && top.name == null) {
                    // the end of a nested begin
                    nested.removeAt(nested.lastIndex)
                } else {
                    reset()
                }
                info
            }

            statement.startsWith("ROLLBACK") -> {
                val info = current()
                reset()
                info
            }

            else -> current()
        }
    }

    // the transaction of a query: the innermost nested one, or the outermost
    private fun current(): FloconTransactionInfo? = if (nested.isEmpty()) {
        transactionId?.let { FloconTransactionInfo(transactionId = it) }
    } else {
        infoOfNestedAt(nested.lastIndex)
    }

    private fun infoOfNestedAt(index: Int): FloconTransactionInfo? {
        val id = transactionId ?: return null
        return FloconTransactionInfo(
            transactionId = id,
            nestedTransactionId = nested[index].id,
            parentTransactionId = if (index >= 1) nested[index - 1].id else id,
            depth = index + 1,
        )
    }

    private fun push(name: String?): FloconTransactionInfo? {
        nested.add(Nested(id = newId(), name = name))
        return current()
    }

    /**
     * The statement belongs to the nested transaction of the savepoint, which ends with the ones it holds.
     * The savepoint that started the outermost transaction ends it, unless [endsRoot] is false.
     */
    private fun release(name: String?, endsRoot: Boolean): FloconTransactionInfo? {
        val index = nested.indexOfLast { it.name != null && it.name == name }
        if (index >= 0) {
            val info = infoOfNestedAt(index)
            while (nested.size > index) nested.removeAt(nested.lastIndex)
            return info
        }
        if (rootSavepoint != null && rootSavepoint == name) {
            val info = transactionId?.let { FloconTransactionInfo(transactionId = it) }
            nested.clear()
            if (endsRoot) reset()
            return info
        }
        // a savepoint we did not see start: the statement is part of what is running
        return current()
    }

    // the name in `SAVEPOINT 'sp_1'`, `RELEASE SAVEPOINT sp_1` or `ROLLBACK TRANSACTION TO SAVEPOINT "sp_1"`: the last word, unquoted
    private fun savepointName(sql: String): String? {
        val last = sql.trim().trimEnd(';').substringAfterLast(' ', missingDelimiterValue = "")
        if (last.isEmpty()) return null
        return when (last.uppercase()) {
            "SAVEPOINT", "TO", "RELEASE", "ROLLBACK", "TRANSACTION" -> null
            else -> last.trim('\'', '"', '`', '[', ']')
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun newId(): String = Uuid.random().toString()

    private fun start(savepoint: String?): FloconTransactionInfo {
        val id = newId()
        transactionId = id
        rootSavepoint = savepoint
        nested.clear()
        return FloconTransactionInfo(transactionId = id)
    }

    private fun reset() {
        transactionId = null
        rootSavepoint = null
        nested.clear()
    }
}
