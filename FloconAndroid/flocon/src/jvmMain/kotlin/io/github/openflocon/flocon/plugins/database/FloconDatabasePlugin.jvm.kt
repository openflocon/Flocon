package io.github.openflocon.flocon.plugins.database

import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.plugins.database.model.FloconDatabaseModel
import io.github.openflocon.flocon.plugins.database.model.fromdevice.DatabaseExecuteSqlResponse
import io.github.openflocon.flocon.plugins.database.model.fromdevice.DeviceDataBaseDataModel
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement
import java.util.Locale

internal actual fun buildFloconDatabaseDataSource(context: FloconContext): FloconDatabaseDataSource {
    return FloconDatabaseDataSourceJvm(context)
}

internal class FloconDatabaseDataSourceJvm(
    private val context: FloconContext
) : FloconDatabaseDataSource {

    override fun executeSQL(
        registeredDatabases: List<FloconDatabaseModel>,
        databaseName: String,
        query: String
    ): DatabaseExecuteSqlResponse {
        var connection: Connection? = null
        return try {
            val dbFile = File(databaseName)
            if (!dbFile.exists()) {
                return DatabaseExecuteSqlResponse.Error(
                    message = "Database file not found: ${dbFile.absolutePath}",
                    originalSql = query
                )
            }

            connection = DriverManager.getConnection("jdbc:sqlite:${dbFile.absolutePath}")
            val firstWord = getFirstWord(query).uppercase(Locale.getDefault())

            when (firstWord) {
                "UPDATE", "DELETE" -> executeUpdateDelete(connection, query)
                "INSERT" -> executeInsert(connection, query)
                "SELECT", "PRAGMA", "EXPLAIN" -> executeSelect(connection, query)
                else -> executeRawQuery(connection, query)
            }
        } catch (t: Throwable) {
            DatabaseExecuteSqlResponse.Error(
                message = t.message ?: "Error executing SQL",
                originalSql = query,
            )
        } finally {
            connection?.close()
        }
    }

    override fun getAllDataBases(
        registeredDatabases: List<FloconDatabaseModel>
    ): List<DeviceDataBaseDataModel> {
        return registeredDatabases.mapNotNull {
            if (File(it.absolutePath).exists()) {
                DeviceDataBaseDataModel(
                    id = it.absolutePath,
                    name = it.displayName,
                )
            } else null
        }

    }
}

// --- SQL execution helpers ---

private fun executeSelect(connection: Connection, query: String): DatabaseExecuteSqlResponse {
    connection.createStatement().use { statement ->
        val resultSet = statement.executeQuery(query)
        val columns = getColumnNames(resultSet)
        val rows = resultSetToList(resultSet, columns.size)
        return DatabaseExecuteSqlResponse.Select(columns, rows)
    }
}

private fun executeUpdateDelete(connection: Connection, query: String): DatabaseExecuteSqlResponse {
    connection.createStatement().use { statement ->
        val count = statement.executeUpdate(query)
        return DatabaseExecuteSqlResponse.UpdateDelete(count)
    }
}

private fun executeInsert(connection: Connection, query: String): DatabaseExecuteSqlResponse {
    connection.createStatement().use { statement ->
        statement.executeUpdate(query, Statement.RETURN_GENERATED_KEYS)
        val keys = statement.generatedKeys
        val id = if (keys.next()) keys.getLong(1) else -1L
        return DatabaseExecuteSqlResponse.Insert(id)
    }
}

private fun executeRawQuery(connection: Connection, query: String): DatabaseExecuteSqlResponse {
    connection.createStatement().use { statement ->
        statement.execute(query)
        return DatabaseExecuteSqlResponse.RawSuccess
    }
}

// --- Utilities ---

private fun getFirstWord(s: String): String {
    val trimmed = s.trim()
    val firstSpace = trimmed.indexOf(' ')
    return if (firstSpace >= 0) trimmed.substring(0, firstSpace) else trimmed
}

private fun getColumnNames(rs: ResultSet): List<String> {
    val meta = rs.metaData
    return (1..meta.columnCount).map { meta.getColumnName(it) }
}

private fun resultSetToList(rs: ResultSet, columnCount: Int): List<List<String?>> {
    val rows = mutableListOf<List<String?>>()
    while (rs.next()) {
        val row = (1..columnCount).map { idx ->
            rs.getObject(idx)?.toString()
        }
        rows.add(row)
    }
    return rows
}