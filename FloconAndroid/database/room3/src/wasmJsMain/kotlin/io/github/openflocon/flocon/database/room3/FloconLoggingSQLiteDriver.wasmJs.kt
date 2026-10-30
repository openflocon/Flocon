package io.github.openflocon.flocon.database.room3

import androidx.sqlite.SQLiteConnection
import androidx.sqlite.SQLiteDriver
import androidx.sqlite.SQLiteStatement
import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.database.core.databasePlugin

actual fun SQLiteDriver.floconLogs(id: String): SQLiteDriver = FloconLoggingSQLiteDriver(delegate = this, dbId = id)

internal class FloconLoggingSQLiteDriver(
    private val delegate: SQLiteDriver,
    private val dbId: String,
) : SQLiteDriver {

    override val hasConnectionPool: Boolean
        get() = delegate.hasConnectionPool

    override suspend fun open(fileName: String): SQLiteConnection = FloconLoggingConnection(delegate.open(fileName), dbId)
}

private class FloconLoggingConnection(
    private val delegate: SQLiteConnection,
    private val dbId: String,
) : SQLiteConnection by delegate {

    override suspend fun prepare(sql: String): SQLiteStatement = FloconLoggingStatement(delegate.prepare(sql), dbId, sql)
}

/**
 * Logs the query when it starts running: the first `step`, and the first one after each `reset`.
 */
private class FloconLoggingStatement(
    private val delegate: SQLiteStatement,
    private val dbId: String,
    private val sql: String,
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

    override suspend fun step(): Boolean {
        if (!logged) {
            logged = true
            Flocon.databasePlugin.logQuery(dbId = dbId, sqlQuery = sql, bindArgs = bindArgs.entries.sortedBy { it.key }.map { it.value })
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
