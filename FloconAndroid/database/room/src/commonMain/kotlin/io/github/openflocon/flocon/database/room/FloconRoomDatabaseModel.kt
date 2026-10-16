package io.github.openflocon.flocon.database.room

import androidx.room.RoomDatabase
import androidx.room.Transactor
import androidx.room.execSQL
import androidx.room.useReaderConnection
import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.database.core.databasePlugin
import io.github.openflocon.flocon.database.core.model.FloconDatabaseModel
import io.github.openflocon.flocon.database.core.model.fromdevice.DatabaseExecuteResponse
import io.github.openflocon.flocon.database.core.model.fromdevice.DatabaseExecuteSqlResponse

fun floconRegisterDatabase(displayName: String, database: RoomDatabase) {
    Flocon.databasePlugin.register(
        FloconRoomDatabaseModel(
            id = displayName,
            displayName = displayName,
            database = database
        )
    )
}

fun floconLogDatabaseQuery(databaseName: String, sqlQuery: String, bindArgs: List<Any?>) {
    Flocon.databasePlugin.logQuery(
        dbName = databaseName,
        sqlQuery = sqlQuery,
        bindArgs = bindArgs,
    )
}

internal data class FloconRoomDatabaseModel(
    override val id: String,
    override val displayName: String,
    val database: RoomDatabase
) : FloconDatabaseModel {

    override suspend fun executeQuery(query: String): DatabaseExecuteResponse {
        return try {
            database.useReaderConnection { connection ->
                val firstWordUpperCase = getFirstWord(query).uppercase()

                when (firstWordUpperCase) {
                    "SELECT",
                    "PRAGMA",
                    "EXPLAIN" -> executeSelect(
                        connection = connection,
                        query = query
                    )

                    "INSERT" -> executeInsert(
                        connection = connection,
                        query = query
                    )

                    "UPDATE",
                    "DELETE" -> executeUpdateDelete(
                        connection = connection,
                        query = query
                    )

                    else -> executeRawQuery(
                        connection = connection,
                        query = query
                    )
                }
            }
        } catch (t: Throwable) {
            DatabaseExecuteSqlResponse.Error(
                message = t.message ?: "error on executeSQL",
                originalSql = query,
            )
        }
    }
}

private suspend fun executeSelect(
    connection: Transactor,
    query: String,
): DatabaseExecuteSqlResponse {
    return connection.usePrepared(query) { statement ->
        val columnNames = mutableListOf<String>()
        val columnCount = statement.getColumnCount()
        for (i in 0 until columnCount) {
            columnNames.add(statement.getColumnName(i))
        }

        val rows = mutableListOf<List<String?>>()
        while (statement.step()) {
            val values = mutableListOf<String?>()
            for (i in 0 until columnCount) {
                values.add(if (statement.isNull(i)) null else statement.getText(i))
            }
            rows.add(values)
        }

        DatabaseExecuteSqlResponse.Select(
            columns = columnNames,
            values = rows
        )
    }
}

private suspend fun executeInsert(
    connection: Transactor,
    query: String,
): DatabaseExecuteSqlResponse {
    connection.execSQL(query)
    val id = connection.usePrepared("SELECT last_insert_rowid()") { it.step(); it.getLong(0) }
    return DatabaseExecuteSqlResponse.Insert(id)
}

private suspend fun executeUpdateDelete(
    connection: Transactor,
    query: String,
): DatabaseExecuteSqlResponse {
    connection.execSQL(query)
    val count = connection.usePrepared("SELECT changes()") { it.step(); it.getLong(0).toInt() }
    return DatabaseExecuteSqlResponse.UpdateDelete(count)
}

private suspend fun executeRawQuery(
    connection: Transactor,
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