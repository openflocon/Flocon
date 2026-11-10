package io.github.openflocon.flocon.database.sqldelight

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlPreparedStatement
import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.database.core.databasePlugin

/**
 * Shows the queries run by a SQLDelight database in the desktop app, without registering it (see [withFlocon] for both).
 *
 * @param id the id the database is registered with, see [register]
 */
fun SqlDriver.floconLogs(id: String): SqlDriver = FloconLoggingSqlDriver(
    delegate = this,
    log = { sql, bindArgs -> Flocon.databasePlugin.logQuery(dbId = id, sqlQuery = sql, bindArgs = bindArgs) },
)

/**
 * Registers the database to browse it from the desktop app, and shows its queries.
 *
 * ```kotlin
 * val driver = AndroidSqliteDriver(AppDatabase.Schema, context, "app.db")
 *     .withFlocon(id = "app", displayName = "App database")
 * val database = AppDatabase(driver)
 * ```
 *
 * @param id unique and stable
 * @param logQueries false to only browse the database
 * @return the driver to give to the database
 */
fun SqlDriver.withFlocon(id: String, displayName: String = id, logQueries: Boolean = true): SqlDriver {
    Flocon.databasePlugin.register(id = id, displayName = displayName, driver = this)
    return if (logQueries) floconLogs(id) else this
}

/**
 * Logs each query once, when it is done (the arguments are only known once the driver has bound them).
 * A failing query is logged too. Async drivers stay async.
 */
internal class FloconLoggingSqlDriver(
    private val delegate: SqlDriver,
    private val log: (sql: String, bindArgs: List<Any?>) -> Unit,
) : SqlDriver by delegate {

    override fun execute(
        identifier: Int?,
        sql: String,
        parameters: Int,
        binders: (SqlPreparedStatement.() -> Unit)?,
    ): QueryResult<Long> = logged(sql) { recorder ->
        delegate.execute(identifier, sql, parameters, recorder.wrap(binders))
    }

    override fun <R> executeQuery(
        identifier: Int?,
        sql: String,
        mapper: (SqlCursor) -> QueryResult<R>,
        parameters: Int,
        binders: (SqlPreparedStatement.() -> Unit)?,
    ): QueryResult<R> = logged(sql) { recorder ->
        delegate.executeQuery(identifier, sql, mapper, parameters, recorder.wrap(binders))
    }

    private fun <T> logged(sql: String, run: (BindRecorder) -> QueryResult<T>): QueryResult<T> {
        val recorder = BindRecorder()
        val result = try {
            run(recorder)
        } catch (t: Throwable) {
            logSafely(sql, recorder)
            throw t
        }
        return when (result) {
            is QueryResult.Value -> result.also { logSafely(sql, recorder) }
            is QueryResult.AsyncValue -> QueryResult.AsyncValue {
                try {
                    result.await()
                } finally {
                    logSafely(sql, recorder)
                }
            }
        }
    }

    // logging must never break a query
    private fun logSafely(sql: String, recorder: BindRecorder) {
        try {
            log(sql, recorder.args())
        } catch (_: Throwable) {
        }
    }
}

// the statements are write only: the arguments can only be recorded when they are bound
private class BindRecorder {
    private val bound = mutableMapOf<Int, Any?>()

    fun args(): List<Any?> = bound.entries.sortedBy { it.key }.map { it.value }

    fun wrap(binders: (SqlPreparedStatement.() -> Unit)?): (SqlPreparedStatement.() -> Unit)? =
        binders?.let { original ->
            {
                // a driver can run the binders more than once (retry): keep the last values
                bound.clear()
                original(RecordingStatement(this, bound))
            }
        }
}

private class RecordingStatement(
    private val delegate: SqlPreparedStatement,
    private val bound: MutableMap<Int, Any?>,
) : SqlPreparedStatement {

    override fun bindBytes(index: Int, bytes: ByteArray?) {
        bound[index] = bytes?.let { "[blob ${it.size} bytes]" }
        delegate.bindBytes(index, bytes)
    }

    override fun bindLong(index: Int, long: Long?) {
        bound[index] = long
        delegate.bindLong(index, long)
    }

    override fun bindDouble(index: Int, double: Double?) {
        bound[index] = double
        delegate.bindDouble(index, double)
    }

    override fun bindString(index: Int, string: String?) {
        bound[index] = string
        delegate.bindString(index, string)
    }

    override fun bindBoolean(index: Int, boolean: Boolean?) {
        bound[index] = boolean
        delegate.bindBoolean(index, boolean)
    }
}
