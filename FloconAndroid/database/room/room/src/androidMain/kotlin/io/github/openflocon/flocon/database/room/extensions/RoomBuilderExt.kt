package io.github.openflocon.flocon.database.room.extensions

import androidx.room.RoomDatabase
import androidx.room.RoomDatabase.QueryCallback
import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.database.core.FloconTransactionState
import io.github.openflocon.flocon.database.core.databasePlugin
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executor
import java.util.concurrent.Executors

inline fun <reified T : RoomDatabase> RoomDatabase.Builder<T>.floconLogs(
    id: String,
    executor: Executor = Executors.newSingleThreadExecutor(),
    queryCallback: QueryCallback? = null
): RoomDatabase.Builder<T> {
    val transactions = FloconRoomTransactions(executor)
    return setQueryCallback(
        queryCallback = { sqlQuery, bindArgs ->
            Flocon.databasePlugin.logQuery(
                dbId = id,
                sqlQuery = sqlQuery,
                bindArgs = bindArgs,
                transactionId = transactions.transactionIdOf(sqlQuery),
            )
            queryCallback?.onQuery(sqlQuery, bindArgs)
        },
        executor = transactions.executor
    )
}

/**
 * Room runs the query callback on [delegate], but a Room transaction lives on the thread that runs its queries: the
 * executor is wrapped to remember, while the callback runs, which thread submitted the query.
 */
@PublishedApi
internal class FloconRoomTransactions(delegate: Executor) {

    private val callerThread = ThreadLocal<Long>()
    private val states = ConcurrentHashMap<Long, FloconTransactionState>()

    val executor: Executor = Executor { runnable ->
        val caller = Thread.currentThread().id
        delegate.execute {
            callerThread.set(caller)
            try {
                runnable.run()
            } finally {
                callerThread.remove()
            }
        }
    }

    /**
     * Must be called from the callback, on the thread of the wrapped executor
     */
    fun transactionIdOf(sql: String): String? {
        val caller = callerThread.get() ?: return null
        val state = states.getOrPut(caller) { FloconTransactionState() }
        val id = state.onQuery(sql)
        if (id == null) {
            // outside of a transaction: no need to keep the state of this thread
            states.remove(caller)
        }
        return id
    }
}
