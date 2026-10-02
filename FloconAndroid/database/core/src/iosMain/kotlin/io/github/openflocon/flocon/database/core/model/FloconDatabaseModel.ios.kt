package io.github.openflocon.flocon.database.core.model

import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.NativeSQLiteDriver
import io.github.openflocon.flocon.database.core.model.fromdevice.DatabaseExecuteSqlResponse

actual fun openDbAndExecuteQuery(
    path: String,
    query: String
): DatabaseExecuteSqlResponse {
    val driver = NativeSQLiteDriver()
    val connection = driver.open(fileName = path)
    return try {
        val firstWord = query.trim().let {
            val idx = it.indexOf(' ')
            if (idx >= 0) it.substring(0, idx) else it
        }.uppercase()

        when (firstWord) {
            "SELECT", "PRAGMA", "EXPLAIN" -> executeSelect(connection, query)
            "INSERT" -> executeInsert(connection, query)
            "UPDATE", "DELETE" -> executeUpdateDelete(connection, query)
            else -> executeRawQuery(connection, query)
        }
    } catch (t: Throwable) {
        DatabaseExecuteSqlResponse.Error(
            message = t.message ?: "Error executing SQL",
            originalSql = query
        )
    } finally {
        connection.close()
    }
}

// --- SQL execution helpers ---

private fun executeSelect(connection: SQLiteConnection, query: String): DatabaseExecuteSqlResponse {
    return connection.prepare(query).use { statement ->
        val columnCount = statement.getColumnCount()
        val columns = (0 until columnCount).map { statement.getColumnName(it) }
        val rows = mutableListOf<List<String?>>()

        while (statement.step()) {
            val row = (0 until columnCount).map { idx ->
                if (statement.isNull(idx)) null else statement.getText(idx)
            }
            rows.add(row)
        }

        DatabaseExecuteSqlResponse.Select(columns, rows)
    }
}

private fun executeInsert(connection: SQLiteConnection, query: String): DatabaseExecuteSqlResponse {
    connection.prepare(query).use { it.step() }

    val id = connection.prepare("SELECT last_insert_rowid()").use { stmt ->
        if (stmt.step()) stmt.getLong(0) else -1L
    }
    return DatabaseExecuteSqlResponse.Insert(id)
}

private fun executeUpdateDelete(connection: SQLiteConnection, query: String): DatabaseExecuteSqlResponse {
    connection.prepare(query).use { it.step() }

    val count = connection.prepare("SELECT changes()").use { stmt ->
        if (stmt.step()) stmt.getLong(0).toInt() else 0
    }
    return DatabaseExecuteSqlResponse.UpdateDelete(affectedCount = count)
}

private fun executeRawQuery(connection: SQLiteConnection, query: String): DatabaseExecuteSqlResponse {
    connection.prepare(query).use { it.step() }
    return DatabaseExecuteSqlResponse.RawSuccess
}