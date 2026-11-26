package io.github.openflocon.domain.database.usecase

import io.github.openflocon.domain.database.models.DatabaseColumnAffinity
import io.github.openflocon.domain.database.models.DatabaseExecuteSqlResponseDomainModel
import io.github.openflocon.domain.database.models.DatabaseRowEditTargetDomainModel
import io.github.openflocon.domain.database.models.DatabaseRowUpdateResultDomainModel
import io.github.openflocon.domain.database.models.DatabaseTableDomainModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class RowEditGuardsTest {

    private fun tableColumn(name: String, type: String, primaryKey: Boolean = false) = DatabaseTableDomainModel.Column(name = name, type = type, nullable = true, primaryKey = primaryKey)

    private val table = listOf(
        tableColumn("a", "INTEGER", primaryKey = true),
        tableColumn("b", "VARCHAR(10)", primaryKey = true),
        tableColumn("price", "DOUBLE"),
        tableColumn("createdAt", "DATETIME"),
        tableColumn("image", "BLOB"),
    )

    @Test
    fun `gives the affinity of the columns of the result, in its order`() {
        val columns = toRowEditColumns(table, listOf("image", "B", "a", "price", "createdAt"))

        assertEquals(
            listOf(
                DatabaseColumnAffinity.Blob,
                DatabaseColumnAffinity.Text,
                DatabaseColumnAffinity.Integer,
                DatabaseColumnAffinity.Real,
                DatabaseColumnAffinity.Numeric,
            ),
            columns?.map { it.affinity },
        )
    }

    @Test
    fun `requires the whole primary key`() {
        assertNotNull(toRowEditColumns(table, listOf("a", "b")))
        assertNull(toRowEditColumns(table, listOf("a", "price")))
        assertNull(toRowEditColumns(listOf(tableColumn("name", "TEXT")), listOf("name")))
    }

    @Test
    fun `rejects unknown and repeated columns, and primary keys which can't be compared`() {
        assertNull(toRowEditColumns(table, listOf("a", "b", "unknown")))
        assertNull(toRowEditColumns(table, listOf("a", "b", "A")))
        assertNull(toRowEditColumns(listOf(tableColumn("id", "REAL", primaryKey = true)), listOf("id")))
        assertNull(toRowEditColumns(listOf(tableColumn("id", "", primaryKey = true)), listOf("id")))
    }

    @Test
    fun `refuses a text in a column of numbers, when it changed`() {
        val columns = listOf(
            DatabaseRowEditTargetDomainModel.Column("count", false, DatabaseColumnAffinity.Integer),
            DatabaseRowEditTargetDomainModel.Column("price", false, DatabaseColumnAffinity.Real),
        )
        val original = listOf("old", "1.5")

        assertNull(findInvalidNumber(columns, original, listOf("old", "-2.5e3")))
        assertNull(findInvalidNumber(columns, original, listOf(null, null)))
        assertNull(findInvalidNumber(columns, original, listOf("-12", "3")))
        listOf("", "abc", "1.5", " 1", "99999999999999999999").forEach {
            assertEquals("count", findInvalidNumber(columns, original, listOf(it, "1.5")), it)
        }
        listOf("", "abc", "NaN", "1d").forEach {
            assertEquals("price", findInvalidNumber(columns, original, listOf("old", it)), it)
        }
    }

    @Test
    fun `an update which affects no row is not a success`() {
        assertEquals(
            DatabaseRowUpdateResultDomainModel.RowNotFound,
            DatabaseExecuteSqlResponseDomainModel.UpdateDelete(0).toRowUpdateResult(),
        )
        assertEquals(
            DatabaseRowUpdateResultDomainModel.Updated,
            DatabaseExecuteSqlResponseDomainModel.UpdateDelete(1).toRowUpdateResult(),
        )
        assertEquals(
            DatabaseRowUpdateResultDomainModel.Failed("readonly"),
            DatabaseExecuteSqlResponseDomainModel.Error("readonly", "UPDATE").toRowUpdateResult(),
        )
    }
}
