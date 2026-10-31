package io.github.openflocon.flocon.database.room.extensions

import androidx.room.RoomDatabase
import androidx.room.RoomDatabase.QueryCallback
import java.util.concurrent.Executor
import java.util.concurrent.Executors

inline fun <reified T : RoomDatabase> RoomDatabase.Builder<T>.floconLogs(
    id: String,
    executor: Executor = Executors.newSingleThreadExecutor(),
    queryCallback: QueryCallback? = null
): RoomDatabase.Builder<T> {
    // no op: only keeps the callback given by the app
    return queryCallback?.let { setQueryCallback(queryCallback = it, executor = executor) } ?: this
}
