package io.github.openflocon.flocon.database.room3.extensions

import androidx.room3.RoomDatabase
import java.util.concurrent.Executor
import java.util.concurrent.Executors

inline fun <reified T : RoomDatabase> RoomDatabase.Builder<T>.floconLogs(
    name: String? = T::class.simpleName,
    executor: Executor = Executors.newSingleThreadExecutor(),
    // QueryCallback is removed or altered in Room 3.
    queryCallback: Any? = null
): RoomDatabase.Builder<T> {
    // TODO: Room 3 removed or changed QueryCallback. Logging must be rewritten for Room 3.
    return this
}