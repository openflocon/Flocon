package io.github.openflocon.flocon.plugins.database

import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.NativeSQLiteDriver
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.plugins.database.model.FloconDatabaseModel
import io.github.openflocon.flocon.plugins.database.model.fromdevice.DatabaseExecuteSqlResponse
import io.github.openflocon.flocon.plugins.database.model.fromdevice.DeviceDataBaseDataModel
import platform.Foundation.NSFileManager
import platform.posix.close

internal actual fun buildFloconDatabaseDataSource(context: FloconContext): FloconDatabaseDataSource {
    return FloconDatabaseDataSourceIos(context)
}

internal class FloconDatabaseDataSourceIos(
    private val context: FloconContext
) : FloconDatabaseDataSource {

    override fun executeSQL(
        registeredDatabases: List<FloconDatabaseModel>,
        databaseName: String,
        query: String
    ): DatabaseExecuteSqlResponse {
        val fileManager = NSFileManager.defaultManager
        if (!fileManager.fileExistsAtPath(databaseName)) {
            return DatabaseExecuteSqlResponse.Error(
                message = "Database file not found: $databaseName",
                originalSql = query
            )
        }

        val driver = NativeSQLiteDriver()
        val connection = driver.open(fileName = databaseName)

        return try {
            val firstWord = getFirstWord(query).uppercase()
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

    override fun getAllDataBases(
        registeredDatabases: List<FloconDatabaseModel>
    ): List<DeviceDataBaseDataModel> {
        val fileManager = NSFileManager.defaultManager
        return registeredDatabases.mapNotNull {
            if (fileManager.fileExistsAtPath(it.absolutePath)) {
                DeviceDataBaseDataModel(
                    id = it.absolutePath,
                    name = it.displayName
                )
            } else null
        }
    }
}

// --- SQL execution helpers ---

private fun executeSelect(connection: SQLiteConnection, query: String): DatabaseExecuteSqlResponse {
    val cursor = connection.prepare(query).use { statement ->
        val columnCount = statement.getColumnCount()
        val columns = (0 until columnCount).map { statement.getColumnName(it) }
        val rows = mutableListOf<List<String?>>()

        while (statement.step()) {
            val row = (0 until columnCount).map { idx ->
                statement.getText(idx)
            }
            rows.add(row)
        }

        statement.close() // maybe remove
        DatabaseExecuteSqlResponse.Select(columns, rows)
    }
    return cursor
}

private fun executeUpdateDelete(connection: SQLiteConnection, query: String): DatabaseExecuteSqlResponse {
    connection.prepare(query).use { statement ->
        statement.close()
    }
    // sqlite-kt n'expose pas encore `changes()`, on renvoie 0
    return DatabaseExecuteSqlResponse.UpdateDelete(affectedCount = 0)
}

private fun executeInsert(connection: SQLiteConnection, query: String): DatabaseExecuteSqlResponse {
    connection.prepare(query).use { statement ->
        statement.close()
    }

    // Récupération du dernier ID inséré
    var id = -1L
    connection.prepare("SELECT last_insert_rowid()").use {
        id = if (it.step()) it.getLong(0) else -1L
        it.close() // maybe remove
    }

    return DatabaseExecuteSqlResponse.Insert(id)
}

private fun executeRawQuery(connection: SQLiteConnection, query: String): DatabaseExecuteSqlResponse {
    connection.prepare(query).use { statement ->
        statement.close() // maybe remove
    }
    return DatabaseExecuteSqlResponse.RawSuccess
}

// --- Utilities ---
private fun getFirstWord(s: String): String {
    val trimmed = s.trim()
    val firstSpace = trimmed.indexOf(' ')
    return if (firstSpace >= 0) trimmed.substring(0, firstSpace) else trimmed
}
