package io.github.openflocon.flocon.database.room

import androidx.room.RoomDatabase
import androidx.room.Transactor
import androidx.room.exclusiveTransaction
import androidx.room.execSQL
import androidx.room.useReaderConnection
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.database.core.databasePlugin
import io.github.openflocon.flocon.database.core.model.FloconSqlDatabaseModel
import io.github.openflocon.flocon.database.core.model.fromdevice.DatabaseExecuteSqlResponse

data class FloconRoomDatabaseModel(
    override val displayName: String,
    val database: RoomDatabase
) : FloconSqlDatabaseModel {
    override suspend fun executeSQL(query: String): DatabaseExecuteSqlResponse {
        return try {
            database.useReaderConnection { connection ->
//                val firstWordUpperCase = getFirstWord(query).uppercase()
//                when (firstWordUpperCase) {
//                    "SELECT", "PRAGMA", "EXPLAIN" -> executeSelect(connection, query)
//                    "INSERT" -> executeInsert(connection, query)
//                    "UPDATE", "DELETE" -> executeUpdateDelete(connection, query)
//                    else -> executeRawQuery(connection, query)
//                }
                TODO()
            }
        } catch (t: Throwable) {
            DatabaseExecuteSqlResponse.Error(
                message = t.message ?: "error on executeSQL",
                originalSql = query,
            )
        }
    }
}

private fun executeSelect(
    connection: Transactor,
    query: String,
): DatabaseExecuteSqlResponse {
    return TODO()
//    connection.prepare(query).use { statement ->
//        val columnNames = mutableListOf<String>()
//        val columnCount = statement.getColumnCount()
//        for (i in 0 until columnCount) {
//            columnNames.add(statement.getColumnName(i))
//        }
//
//        val rows = mutableListOf<List<String?>>()
//        while (statement.step()) {
//            val values = mutableListOf<String?>()
//            for (i in 0 until columnCount) {
//                values.add(if (statement.isNull(i)) null else statement.getText(i))
//            }
//            rows.add(values)
//        }
//
//        DatabaseExecuteSqlResponse.Select(
//            columns = columnNames,
//            values = rows,
//        )
//    }
}

private fun executeInsert(
    connection: SQLiteConnection,
    query: String,
): DatabaseExecuteSqlResponse {
    connection.execSQL(query)
    // SQLite doesn't easily return the last inserted ID via the statement itself without extra queries like last_insert_rowid()
    // But for inspection purposes, we might just return 0 or query it.
    // For now, let's just return a successful RawSuccess or implement last_insert_rowid
    val id = connection.prepare("SELECT last_insert_rowid()").use { it.step(); it.getLong(0) }
    return DatabaseExecuteSqlResponse.Insert(id)
}

private fun executeUpdateDelete(
    connection: SQLiteConnection,
    query: String,
): DatabaseExecuteSqlResponse {
    connection.execSQL(query)
    val count = connection.prepare("SELECT changes()").use { it.step(); it.getLong(0).toInt() }
    return DatabaseExecuteSqlResponse.UpdateDelete(count)
}

private fun executeRawQuery(
    connection: SQLiteConnection,
    query: String,
): DatabaseExecuteSqlResponse {
    connection.execSQL(query)
    return DatabaseExecuteSqlResponse.RawSuccess
}

private fun getFirstWord(s: String): String {
    val trimmed = s.trim()
    val firstSpace = trimmed.indexOf(' ')
    return if (firstSpace >= 0) trimmed.substring(0, firstSpace) else trimmed
}

fun floconRegisterDatabase(displayName: String, database: RoomDatabase) {
    Flocon.databasePlugin.register(
        FloconRoomDatabaseModel(
            displayName = displayName,
            database = database,
        )
    )
}
