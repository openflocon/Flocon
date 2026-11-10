package io.github.openflocon.flocon.database.sqldelight

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlPreparedStatement
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class FloconLoggingSqlDriverTest {

    private data class Log(val sql: String, val bindArgs: List<Any?>)

    private val logs = mutableListOf<Log>()
    private val jdbc = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)

    private fun logging(delegate: SqlDriver = jdbc, failingLogger: Boolean = false) =
        FloconLoggingSqlDriver(delegate) { sql, args ->
            if (failingLogger) error("logger down")
            logs += Log(sql, args)
        }

    private fun SqlDriver.run(sql: String, binders: (SqlPreparedStatement.() -> Unit)? = null) =
        runBlocking { execute(null, sql, 0, binders).await() }

    private fun SqlDriver.dogNames(binders: SqlPreparedStatement.() -> Unit) = runBlocking {
        executeQuery(null, "SELECT name FROM dog WHERE age > ?", { c: SqlCursor ->
            val names = mutableListOf<String?>()
            while (c.next().value) names += c.getString(0)
            QueryResult.Value(names)
        }, 1, binders).await()
    }

    @Test
    fun `execute is logged once with its arguments of every type`() {
        val driver = logging()
        driver.run("CREATE TABLE dog (name TEXT, age INTEGER, weight REAL, photo BLOB, good INTEGER, note TEXT)")
        logs.clear()

        driver.run("INSERT INTO dog VALUES (?, ?, ?, ?, ?, ?)") {
            bindString(0, "Rex")
            bindLong(1, 4)
            bindDouble(2, 12.5)
            bindBytes(3, byteArrayOf(1, 2, 3))
            bindBoolean(4, true)
            bindString(5, null)
        }

        assertEquals(
            listOf(Log("INSERT INTO dog VALUES (?, ?, ?, ?, ?, ?)", listOf("Rex", 4L, 12.5, "[blob 3 bytes]", true, null))),
            logs,
        )
    }

    @Test
    fun `executeQuery is logged and still returns its rows`() {
        val driver = logging()
        driver.run("CREATE TABLE dog (name TEXT, age INTEGER)")
        driver.run("INSERT INTO dog VALUES ('Rex', 4)")
        logs.clear()

        assertEquals(listOf<String?>("Rex"), driver.dogNames { bindLong(0, 1) })
        assertEquals(listOf(Log("SELECT name FROM dog WHERE age > ?", listOf(1L))), logs)
    }

    @Test
    fun `a failing query is logged and its error is kept`() {
        val driver = logging()
        assertFailsWith<Exception> { driver.run("SELEC nope") }
        assertEquals(listOf(Log("SELEC nope", emptyList())), logs)
    }

    @Test
    fun `a failing logger does not break the query`() {
        val driver = logging(failingLogger = true)
        driver.run("CREATE TABLE dog (name TEXT)")
        assertEquals(emptyList(), logs)
    }

    @Test
    fun `an async result is logged when it resolves and stays async`() {
        var resolved = false
        val async = object : SqlDriver by jdbc {
            override fun execute(
                identifier: Int?, sql: String, parameters: Int, binders: (SqlPreparedStatement.() -> Unit)?,
            ): QueryResult<Long> = QueryResult.AsyncValue {
                resolved = true
                jdbc.execute(identifier, sql, parameters, binders).value
            }
        }
        val result = logging(async).execute(null, "CREATE TABLE dog (name TEXT)", 0, null)

        assertEquals(emptyList(), logs)
        assertEquals(false, resolved)
        runBlocking { result.await() }
        assertEquals(listOf(Log("CREATE TABLE dog (name TEXT)", emptyList())), logs)
    }
}
