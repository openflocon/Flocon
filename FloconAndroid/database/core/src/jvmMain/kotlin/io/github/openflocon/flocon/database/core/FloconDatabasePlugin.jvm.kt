package io.github.openflocon.flocon.database.core.model

import io.github.openflocon.flocon.database.core.model.fromdevice.DatabaseExecuteSqlResponse
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet

actual fun openDbAndExecuteQuery(
    path: String,
    query: String
): DatabaseExecuteSqlResponse {
    var connection: Connection? = null
    return try {
        Class.forName("org.sqlite.JDBC")
        connection = DriverManager.getConnection("jdbc:sqlite:$path")
        executeSQLInternal(connection, query)
    } catch (t: Throwable) {
        DatabaseExecuteSqlResponse.Error(
            message = t.message ?: "error on executeSQL",
            originalSql = query,
        )
    } finally {
        connection?.close()
    }
}

private fun executeSQLInternal(
    connection: Connection,
    query: String
): DatabaseExecuteSqlResponse {
    val firstWord = query.trim().let {
        val idx = it.indexOf(' ')
        if (idx >= 0) it.substring(0, idx) else it
    }.uppercase()

    return when (firstWord) {
        "SELECT", "PRAGMA", "EXPLAIN" -> executeSelect(connection, query)
        "INSERT" -> executeInsert(connection, query)
        "UPDATE", "DELETE" -> executeUpdateDelete(connection, query)
        else -> executeRawQuery(connection, query)
    }
}

private fun executeSelect(
    connection: Connection,
    query: String,
): DatabaseExecuteSqlResponse {
    val statement = connection.createStatement()
    val rs: ResultSet = statement.executeQuery(query)
    val meta = rs.metaData
    val columnCount = meta.columnCount
    val columns = (1..columnCount).map { meta.getColumnName(it) }
    val rows = mutableListOf<List<String?>>()
    while (rs.next()) {
        val row = (1..columnCount).map { i ->
            rs.getString(i)
        }
        rows.add(row)
    }
    return DatabaseExecuteSqlResponse.Select(columns = columns, values = rows)
}

private fun executeInsert(
    connection: Connection,
    query: String,
): DatabaseExecuteSqlResponse {
    val statement = connection.createStatement()
    statement.executeUpdate(query)
    val keys: ResultSet = statement.generatedKeys
    val insertedId = if (keys.next()) keys.getLong(1) else -1L
    return DatabaseExecuteSqlResponse.Insert(insertedId)
}

private fun executeUpdateDelete(
    connection: Connection,
    query: String,
): DatabaseExecuteSqlResponse {
    val statement = connection.createStatement()
    val count = statement.executeUpdate(query)
    return DatabaseExecuteSqlResponse.UpdateDelete(count)
}

private fun executeRawQuery(
    connection: Connection,
    query: String,
): DatabaseExecuteSqlResponse {
    connection.createStatement().execute(query)
    return DatabaseExecuteSqlResponse.RawSuccess
}