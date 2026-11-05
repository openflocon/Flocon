package io.github.openflocon.flocon.database.room.extensions

import androidx.room.RoomDatabase
import androidx.room.RoomDatabase.QueryCallback
import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.database.core.databasePlugin
import java.util.concurrent.Executor
import java.util.concurrent.Executors

inline fun <reified T : RoomDatabase> RoomDatabase.Builder<T>.floconLogs(
    id: String,
    executor: Executor = Executors.newSingleThreadExecutor(),
    queryCallback: QueryCallback? = null
): RoomDatabase.Builder<T> {
    // the plugin finds the transaction of a query from the ones logged before, they must reach it one at a time
    val lock = Any()
    return setQueryCallback(
        queryCallback = { sqlQuery, bindArgs ->
            synchronized(lock) {
                Flocon.databasePlugin.logQuery(dbId = id, sqlQuery = sqlQuery, bindArgs = bindArgs)
            }
            queryCallback?.onQuery(sqlQuery, bindArgs)
        },
        executor = executor
    )
}
