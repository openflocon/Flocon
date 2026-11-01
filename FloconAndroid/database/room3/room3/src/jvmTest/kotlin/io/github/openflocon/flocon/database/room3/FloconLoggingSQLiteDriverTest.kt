package io.github.openflocon.flocon.database.room3

import androidx.sqlite.SQLiteConnection
import androidx.sqlite.SQLiteDriver
import androidx.sqlite.SQLiteStatement
import kotlin.test.Test
import kotlin.test.assertEquals

class FloconLoggingSQLiteDriverTest {

    private data class Log(val dbId: String, val sql: String, val bindArgs: List<Any?>)

    private val logs = mutableListOf<Log>()
    private val statement = FakeStatement()
    private val driver = FloconLoggingSQLiteDriver(
        delegate = object : SQLiteDriver {
            override fun open(fileName: String): SQLiteConnection = object : SQLiteConnection {
                override fun prepare(sql: String): SQLiteStatement = statement
                override fun close() = Unit
            }
        },
        dbId = "dogs",
        logger = { dbId, sql, bindArgs -> logs += Log(dbId, sql, bindArgs) },
    )

    private fun prepare(sql: String) = driver.open("dogs.db").prepare(sql)

    @Test
    fun `the query is logged with its id and its arguments when it runs`() {
        val prepared = prepare("INSERT INTO dog (name, age) VALUES (?, ?)")
        prepared.bindText(1, "Rex")
        prepared.bindLong(2, 4)
        assertEquals(emptyList(), logs)

        prepared.step()

        assertEquals(listOf(Log("dogs", "INSERT INTO dog (name, age) VALUES (?, ?)", listOf("Rex", 4L))), logs)
    }

    @Test
    fun `the arguments are in the order of their index`() {
        val prepared = prepare("SELECT * FROM dog WHERE age > ? AND name = ?")
        prepared.bindText(2, "Rex")
        prepared.bindNull(1)

        prepared.step()

        assertEquals(listOf<Any?>(null, "Rex"), logs.single().bindArgs)
    }

    @Test
    fun `a query is logged once per run, not once per row`() {
        val prepared = prepare("SELECT * FROM dog")

        prepared.step()
        prepared.step()
        prepared.step()
        assertEquals(1, logs.size)

        prepared.reset()
        prepared.step()
        assertEquals(2, logs.size)
    }

    @Test
    fun `the statement still runs on the wrapped driver`() {
        val prepared = prepare("SELECT 1")
        prepared.bindLong(1, 1)

        prepared.step()

        assertEquals(listOf("bindLong 1", "step"), statement.calls)
    }

    private class FakeStatement : SQLiteStatement {
        val calls = mutableListOf<String>()
        override fun bindBlob(index: Int, value: ByteArray) { calls += "bindBlob $index" }
        override fun bindDouble(index: Int, value: Double) { calls += "bindDouble $index" }
        override fun bindLong(index: Int, value: Long) { calls += "bindLong $index" }
        override fun bindText(index: Int, value: String) { calls += "bindText $index" }
        override fun bindNull(index: Int) { calls += "bindNull $index" }
        override fun getBlob(index: Int): ByteArray = ByteArray(0)
        override fun getDouble(index: Int): Double = 0.0
        override fun getLong(index: Int): Long = 0
        override fun getText(index: Int): String = ""
        override fun isNull(index: Int): Boolean = false
        override fun getColumnCount(): Int = 0
        override fun getColumnName(index: Int): String = ""
        override fun getColumnType(index: Int): Int = 0
        override fun step(): Boolean { calls += "step"; return false }
        override fun reset() { calls += "reset" }
        override fun clearBindings() { calls += "clearBindings" }
        override fun close() = Unit
    }
}
