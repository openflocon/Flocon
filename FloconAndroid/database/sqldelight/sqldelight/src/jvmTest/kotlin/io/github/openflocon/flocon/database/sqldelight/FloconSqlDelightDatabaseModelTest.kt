package io.github.openflocon.flocon.database.sqldelight

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.openflocon.flocon.database.core.model.fromdevice.DatabaseExecuteSqlResponse
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class FloconSqlDelightDatabaseModelTest {

    private val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
    private val model = FloconSqlDelightDatabaseModel("dogs", "Dogs", driver)

    private fun run(sql: String) = runBlocking { model.executeQuery(sql) }

    private fun select(sql: String): DatabaseExecuteSqlResponse.Select {
        val result = run(sql)
        assertIs<DatabaseExecuteSqlResponse.Select>(result)
        return result
    }

    private fun withDogs(): FloconSqlDelightDatabaseModel {
        run("CREATE TABLE dog (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, age INTEGER)")
        run("INSERT INTO dog (name, age) VALUES ('Rex', 4)")
        run("INSERT INTO dog (name, age) VALUES (NULL, 7)")
        return model
    }

    @Test
    fun `select returns column names, values and nulls`() {
        withDogs()
        val result = select("SELECT name, age AS years FROM dog ORDER BY id;")
        assertEquals(listOf("name", "years"), result.columns)
        assertEquals(listOf(listOf("Rex", "4"), listOf(null, "7")), result.values)
    }

    @Test
    fun `select with no row still returns the column names`() {
        withDogs()
        val result = select("SELECT id, name FROM dog WHERE age > 100")
        assertEquals(listOf("id", "name"), result.columns)
        assertEquals(emptyList(), result.values)
    }

    @Test
    fun `no temporary view is left behind`() {
        withDogs()
        select("SELECT * FROM dog")
        assertEquals(emptyList(), select("SELECT name FROM sqlite_temp_master").values)
    }

    @Test
    fun `pragma falls back to the probed column count`() {
        withDogs()
        val result = select("PRAGMA table_info(dog)")
        assertEquals(6, result.columns.size)
        assertEquals(3, result.values.size)
        assertEquals("name", result.values[1][1])
    }

    @Test
    fun `insert returns the inserted id`() {
        withDogs()
        val result = run("INSERT INTO dog (name, age) VALUES ('Fido', 2)")
        assertIs<DatabaseExecuteSqlResponse.Insert>(result)
        assertEquals(3L, result.insertedId)
    }

    @Test
    fun `update and delete return the affected rows`() {
        withDogs()
        val update = run("UPDATE dog SET age = 1")
        assertIs<DatabaseExecuteSqlResponse.UpdateDelete>(update)
        assertEquals(2, update.affectedCount)
        val delete = run("DELETE FROM dog WHERE id = 1")
        assertIs<DatabaseExecuteSqlResponse.UpdateDelete>(delete)
        assertEquals(1, delete.affectedCount)
    }

    @Test
    fun `raw statements succeed`() {
        assertIs<DatabaseExecuteSqlResponse.RawSuccess>(run("CREATE TABLE cat (id INTEGER)"))
    }

    @Test
    fun `a syntax error is returned as an error`() {
        val result = run("SELEC nope")
        assertIs<DatabaseExecuteSqlResponse.Error>(result)
        assertEquals("SELEC nope", result.originalSql)
    }
}
