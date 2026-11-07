package io.github.openflocon.flocondesktop.features.timeline.mapper

import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineItemUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.previewTimelineRowUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.previewTimelineTransaction
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TransactionGroupTest {

    private fun query(id: String, transactionId: String? = null, isControl: Boolean = false) = TimelineItemUiModel.Row(
        previewTimelineRowUiModel(
            source = TimelineSource.Database,
            id = id,
            title = "SELECT $id",
            subtitle = "app.db",
            transaction = transactionId?.let { previewTimelineTransaction(id = it, isControl = isControl) },
        ),
    )

    @Test
    fun `the header goes above the first query of a transaction`() {
        val header = transactionHeaderBetween(before = query("0"), after = query("1", "tx"), inverted = false, collapsed = emptySet())

        assertEquals("tx", assertIs<TimelineItemUiModel.TransactionHeader>(header).transaction.id)
        assertEquals("app.db", header.databaseName)
    }

    @Test
    fun `the header goes above the oldest query of a transaction when the list is inverted`() {
        // inverted: the older item is displayed above, so the header goes after the last query of the transaction
        assertNull(transactionHeaderBetween(before = query("0"), after = query("1", "tx"), inverted = true, collapsed = emptySet()))
        assertEquals(
            "tx",
            transactionHeaderBetween(before = query("1", "tx"), after = query("0"), inverted = true, collapsed = emptySet())?.transaction?.id,
        )
    }

    @Test
    fun `a transaction of one query has no header, its statements are still hidden`() {
        val begin = TimelineItemUiModel.Row(
            previewTimelineRowUiModel(
                source = TimelineSource.Database,
                id = "b",
                transaction = previewTimelineTransaction(id = "tx", queryCount = 1, isControl = true),
            ),
        )

        assertNull(transactionHeaderBetween(before = query("0"), after = begin, inverted = false, collapsed = emptySet()))
        assertTrue(begin.isHiddenInTransaction(collapsed = emptySet()))
    }

    @Test
    fun `the queries of a transaction have one header`() {
        assertNull(transactionHeaderBetween(before = query("1", "tx"), after = query("2", "tx"), inverted = false, collapsed = emptySet()))
        assertNull(transactionHeaderBetween(before = query("1", "tx"), after = query("2", "tx"), inverted = true, collapsed = emptySet()))
    }

    @Test
    fun `two transactions in a row have a header each`() {
        assertNotEquals(null, transactionHeaderBetween(before = query("1", "tx1"), after = query("2", "tx2"), inverted = false, collapsed = emptySet()))
    }

    @Test
    fun `a query outside of a transaction has no header`() {
        assertNull(transactionHeaderBetween(before = query("1", "tx"), after = query("2"), inverted = false, collapsed = emptySet()))
        assertNull(transactionHeaderBetween(before = null, after = null, inverted = false, collapsed = emptySet()))
    }

    @Test
    fun `the header says if the transaction shows its queries`() {
        assertTrue(transactionHeaderBetween(null, query("1", "tx"), inverted = false, collapsed = emptySet())!!.isExpanded)
        assertFalse(transactionHeaderBetween(null, query("1", "tx"), inverted = false, collapsed = setOf("tx"))!!.isExpanded)
    }

    @Test
    fun `the headers of a transaction interrupted by another event have different keys`() {
        val first = transactionHeaderBetween(null, query("1", "tx"), inverted = false, collapsed = emptySet())!!
        val second = transactionHeaderBetween(query("0"), query("2", "tx"), inverted = false, collapsed = emptySet())!!

        assertNotEquals(first.key, second.key)
    }

    @Test
    fun `the queries of a folded transaction are hidden`() {
        assertFalse(query("1", "tx").isHiddenInTransaction(collapsed = emptySet()))
        assertTrue(query("1", "tx").isHiddenInTransaction(collapsed = setOf("tx")))
    }

    @Test
    fun `the statements that open and close a transaction are always hidden`() {
        assertTrue(query("1", "tx", isControl = true).isHiddenInTransaction(collapsed = setOf("tx")))
    }

    @Test
    fun `the events outside of a transaction are never hidden`() {
        assertFalse(query("1").isHiddenInTransaction(collapsed = emptySet()))
        assertFalse(TimelineItemUiModel.DayHeader(day = "20/10/2026", key = "day").isHiddenInTransaction(collapsed = emptySet()))
    }
}
