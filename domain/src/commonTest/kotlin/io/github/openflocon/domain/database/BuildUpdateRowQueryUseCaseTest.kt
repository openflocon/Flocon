package io.github.openflocon.domain.database

import io.github.openflocon.domain.database.models.DatabaseColumnAffinity
import io.github.openflocon.domain.database.models.DatabaseRowEditTargetDomainModel
import io.github.openflocon.domain.database.usecase.BuildUpdateRowQueryUseCase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class BuildUpdateRowQueryUseCaseTest {

    private val build = BuildUpdateRowQueryUseCase()

    private fun column(
        name: String,
        isPrimaryKey: Boolean = false,
        affinity: DatabaseColumnAffinity = DatabaseColumnAffinity.Text,
    ) = DatabaseRowEditTargetDomainModel.Column(name, isPrimaryKey, affinity, isNullable = true)

    private fun target(vararg columns: DatabaseRowEditTargetDomainModel.Column) = DatabaseRowEditTargetDomainModel(
        deviceId = "device",
        packageName = "app",
        databaseId = "db",
        tableName = "users",
        columns = columns.toList(),
    )

    private val users = target(column("id", isPrimaryKey = true), column("name"), column("city"))

    @Test
    fun `writes only the changed values and checks the previous ones`() {
        assertEquals(
            """UPDATE "users" SET "name" = 'Bob' WHERE "id" = '1' AND "name" IS 'Alice'""",
            build(users, original = listOf("1", "Alice", "Paris"), edited = listOf("1", "Bob", "Paris")),
        )
    }

    @Test
    fun `tells NULL, an empty text and the text NULL apart`() {
        assertEquals(
            """UPDATE "users" SET "name" = NULL, "city" = '' WHERE "id" = '1' AND "name" IS 'NULL' AND "city" IS NULL""",
            build(users, original = listOf("1", "NULL", null), edited = listOf("1", null, "")),
        )
    }

    @Test
    fun `escapes the values and the identifiers`() {
        val target = target(column("id", isPrimaryKey = true), column("the \"name\""))

        // the name column, quoted: each of its quotes is doubled
        val name = "\"the \"\"name\"\"\""

        assertEquals(
            "UPDATE \"users\" SET $name = 'x''; DROP TABLE users; --' WHERE \"id\" = 'O''Brien' AND $name IS 'a'",
            build(target, original = listOf("O'Brien", "a"), edited = listOf("O'Brien", "x'; DROP TABLE users; --")),
        )
    }

    @Test
    fun `identifies the row with its whole primary key, with its previous value when it changes`() {
        val target = target(column("a", isPrimaryKey = true), column("b", isPrimaryKey = true))

        assertEquals(
            """UPDATE "users" SET "b" = '3' WHERE "a" = '1' AND "b" = '2'""",
            build(target, original = listOf("1", "2"), edited = listOf("1", "3")),
        )
    }

    @Test
    fun `does not compare the decimal values`() {
        val target = target(column("id", isPrimaryKey = true), column("price", affinity = DatabaseColumnAffinity.Real))

        assertEquals(
            """UPDATE "users" SET "price" = '2.5' WHERE "id" = '1'""",
            build(target, original = listOf("1", "1.33333"), edited = listOf("1", "2.5")),
        )
    }

    @Test
    fun `builds nothing without a change, a primary key or its value`() {
        val row = listOf("1", "Alice", "Paris")
        assertNull(build(users, original = row, edited = row))
        assertNull(build(users, original = listOf(null, "Alice", "Paris"), edited = listOf(null, "Bob", "Paris")))
        assertNull(build(target(column("name")), original = listOf("a"), edited = listOf("b")))
        assertNull(build(target(column("id", isPrimaryKey = true), column("blob", affinity = DatabaseColumnAffinity.Blob)), listOf("1", "a"), listOf("1", "b")))
    }
}
