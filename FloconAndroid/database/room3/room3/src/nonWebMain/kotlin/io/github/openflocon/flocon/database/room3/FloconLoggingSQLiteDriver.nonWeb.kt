package io.github.openflocon.flocon.database.room3

import androidx.sqlite.SQLiteConnection
import androidx.sqlite.SQLiteDriver
import androidx.sqlite.SQLiteStatement

actual fun SQLiteDriver.floconLogs(id: String): SQLiteDriver = FloconLoggingSQLiteDriver(delegate = this, dbId = id, logger = DatabasePluginQueryLogger)

internal class FloconLoggingSQLiteDriver(
    private val delegate: SQLiteDriver,
    private val dbId: String,
    private val logger: QueryLogger,
) : SQLiteDriver {

    override val hasConnectionPool: Boolean
        get() = delegate.hasConnectionPool

    override fun open(fileName: String): SQLiteConnection = FloconLoggingConnection(delegate.open(fileName), dbId, logger)
}

private class FloconLoggingConnection(
    private val delegate: SQLiteConnection,
    private val dbId: String,
    private val logger: QueryLogger,
) : SQLiteConnection by delegate {

    override fun prepare(sql: String): SQLiteStatement = FloconLoggingStatement(delegate.prepare(sql), dbId, sql, logger)
}

/**
 * Logs the query when it starts running: the first `step`, and the first one after each `reset`.
 */
private class FloconLoggingStatement(
    private val delegate: SQLiteStatement,
    private val dbId: String,
    private val sql: String,
    private val logger: QueryLogger,
) : SQLiteStatement by delegate {

    private val bindArgs = mutableMapOf<Int, Any?>()
    private var logged = false

    override fun bindBlob(index: Int, value: ByteArray) {
        bindArgs[index] = "[blob ${value.size} bytes]"
        delegate.bindBlob(index, value)
    }

    override fun bindDouble(index: Int, value: Double) {
        bindArgs[index] = value
        delegate.bindDouble(index, value)
    }

    override fun bindLong(index: Int, value: Long) {
        bindArgs[index] = value
        delegate.bindLong(index, value)
    }

    override fun bindText(index: Int, value: String) {
        bindArgs[index] = value
        delegate.bindText(index, value)
    }

    override fun bindNull(index: Int) {
        bindArgs[index] = null
        delegate.bindNull(index)
    }

    override fun step(): Boolean {
        if (!logged) {
            logged = true
            logger.log(dbId = dbId, sql = sql, bindArgs = bindArgs.entries.sortedBy { it.key }.map { it.value })
        }
        return delegate.step()
    }

    override fun reset() {
        logged = false
        delegate.reset()
    }

    override fun clearBindings() {
        bindArgs.clear()
        delegate.clearBindings()
    }
}
