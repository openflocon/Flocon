package io.github.openflocon.flocon.database.room.extensions

import androidx.room.RoomDatabase
import androidx.room.RoomDatabase.QueryCallback
import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.database.core.databasePlugin
import java.util.concurrent.Executor
import java.util.concurrent.Executors

inline fun <reified T : RoomDatabase> RoomDatabase.Builder<T>.floconLogs(
    name: String? = T::class.simpleName,
    executor: Executor = Executors.newSingleThreadExecutor(),
    queryCallback: QueryCallback? = null
): RoomDatabase.Builder<T> {
    return setQueryCallback(
        queryCallback = { sqlQuery, bindArgs ->
            Flocon.databasePlugin.logQuery(
                dbName = name ?: "database",
                sqlQuery = sqlQuery,
                bindArgs = bindArgs
            )
            queryCallback?.onQuery(sqlQuery, bindArgs)
        },
        executor = executor
    )
}