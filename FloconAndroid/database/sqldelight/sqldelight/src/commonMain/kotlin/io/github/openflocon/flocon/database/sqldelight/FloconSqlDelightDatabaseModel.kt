package io.github.openflocon.flocon.database.sqldelight

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.db.SqlDriver
import io.github.openflocon.flocon.database.core.FloconDatabasePlugin
import io.github.openflocon.flocon.database.core.model.FloconDatabaseModel
import io.github.openflocon.flocon.database.core.model.fromdevice.DatabaseExecuteResponse
import io.github.openflocon.flocon.database.core.model.fromdevice.DatabaseExecuteSqlResponse
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.random.Random

/**
 * Registers a SQLDelight database, to browse it from the desktop app
 *
 * @param id unique and stable
 */
fun FloconDatabasePlugin.register(id: String, displayName: String, driver: SqlDriver) {
    register(
        FloconSqlDelightDatabaseModel(
            id = id,
            displayName = displayName,
            driver = driver,
        )
    )
}

internal data class FloconSqlDelightDatabaseModel(
    override val id: String,
    override val displayName: String,
    val driver: SqlDriver,
) : FloconDatabaseModel {

    override suspend fun executeQuery(query: String): DatabaseExecuteResponse = withContext(Dispatchers.Default) {
        try {
            when (firstWord(query).uppercase()) {
                "SELECT",
                "WITH",
                "PRAGMA",
                "EXPLAIN" -> driver.select(query)

                "INSERT" -> {
                    driver.run(query)
                    DatabaseExecuteSqlResponse.Insert(driver.singleLong("SELECT last_insert_rowid()"))
                }

                // execute() returns the affected rows
                "UPDATE",
                "DELETE" -> DatabaseExecuteSqlResponse.UpdateDelete(driver.run(query).toInt())

                else -> {
                    driver.run(query)
                    DatabaseExecuteSqlResponse.RawSuccess
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (t: Throwable) {
            DatabaseExecuteSqlResponse.Error(
                message = t.message ?: "error on executeSQL",
                originalSql = query,
            )
        }
    }
}

// always await: free on sync drivers, required on async ones (web)
private suspend fun SqlDriver.run(sql: String): Long =
    execute(null, sql, 0, null).await()

private suspend fun SqlDriver.singleLong(sql: String): Long =
    executeQuery(
        identifier = null,
        sql = sql,
        mapper = { cursor -> QueryResult.Value(if (cursor.next().value) cursor.getLong(0) ?: 0L else 0L) },
        parameters = 0,
    ).await()

private suspend fun SqlDriver.select(query: String): DatabaseExecuteSqlResponse {
    val sql = query.trim().trimEnd(';')
    // SqlCursor has no column names nor column count (checked on 2.4.1)
    val names = columnNames(sql)
    return executeQuery(
        identifier = null,
        sql = sql,
        mapper = { cursor ->
            val rows = mutableListOf<List<String?>>()
            var count = names?.size
            while (cursor.next().value) {
                val columnCount = count ?: cursor.probeColumnCount().also { count = it }
                rows += List(columnCount) { cursor.getString(it) }
            }
            val columns = names ?: List(count ?: 0) { "column_$it" }
            QueryResult.Value(DatabaseExecuteSqlResponse.Select(columns = columns, values = rows))
        },
        parameters = 0,
    ).await()
}

/**
 * The names of the columns of a SELECT, read through a temporary view and `PRAGMA table_info`.
 * Null when it cannot be done (PRAGMA, EXPLAIN, several statements...).
 */
private suspend fun SqlDriver.columnNames(select: String): List<String>? {
    if (firstWord(select).uppercase() !in listOf("SELECT", "WITH")) return null
    val view = "flocon_columns_${Random.nextLong().toULong()}"
    return try {
        run("CREATE TEMP VIEW $view AS $select")
        try {
            executeQuery(
                identifier = null,
                sql = "PRAGMA table_info($view)",
                mapper = { cursor ->
                    val names = mutableListOf<String>()
                    while (cursor.next().value) names += cursor.getString(1).orEmpty()
                    QueryResult.Value(names)
                },
                parameters = 0,
            ).await()
        } finally {
            run("DROP VIEW IF EXISTS $view")
        }
    } catch (e: CancellationException) {
        throw e
    } catch (_: Throwable) {
        null
    }
}

// the drivers throw when the index is past the last column
private fun SqlCursor.probeColumnCount(): Int {
    var count = 0
    try {
        while (true) {
            getString(count)
            count++
        }
    } catch (_: Throwable) {
        return count
    }
}

private fun firstWord(s: String): String {
    val trimmed = s.trim()
    val end = trimmed.indexOfFirst { it.isWhitespace() }
    return if (end >= 0) trimmed.substring(0, end) else trimmed
}
