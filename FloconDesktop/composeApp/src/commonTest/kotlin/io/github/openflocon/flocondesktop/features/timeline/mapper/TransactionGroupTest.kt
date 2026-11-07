package io.github.openflocon.flocondesktop.features.timeline.mapper

import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.flocondesktop.common.ui.TransactionFolding
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
        val header = transactionHeaderBetween(before = query("0"), after = query("1", "tx"), inverted = false, folding = TransactionFolding())

        assertEquals("tx", assertIs<TimelineItemUiModel.TransactionHeader>(header).transaction.id)
        assertEquals("app.db", header.databaseName)
    }

    @Test
    fun `the header goes above the oldest query of a transaction when the list is inverted`() {
        // inverted: the older item is displayed above, so the header goes after the last query of the transaction
        assertNull(transactionHeaderBetween(before = query("0"), after = query("1", "tx"), inverted = true, folding = TransactionFolding()))
        assertEquals(
            "tx",
            transactionHeaderBetween(before = query("1", "tx"), after = query("0"), inverted = true, folding = TransactionFolding())?.transaction?.id,
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

        assertNull(transactionHeaderBetween(before = query("0"), after = begin, inverted = false, folding = TransactionFolding()))
        assertTrue(begin.isHiddenInTransaction(folding = TransactionFolding()))
    }

    @Test
    fun `the queries of a transaction have one header`() {
        assertNull(transactionHeaderBetween(before = query("1", "tx"), after = query("2", "tx"), inverted = false, folding = TransactionFolding()))
        assertNull(transactionHeaderBetween(before = query("1", "tx"), after = query("2", "tx"), inverted = true, folding = TransactionFolding()))
    }

    @Test
    fun `two transactions in a row have a header each`() {
        assertNotEquals(null, transactionHeaderBetween(before = query("1", "tx1"), after = query("2", "tx2"), inverted = false, folding = TransactionFolding()))
    }

    @Test
    fun `a query outside of a transaction has no header`() {
        assertNull(transactionHeaderBetween(before = query("1", "tx"), after = query("2"), inverted = false, folding = TransactionFolding()))
        assertNull(transactionHeaderBetween(before = null, after = null, inverted = false, folding = TransactionFolding()))
    }

    @Test
    fun `the header says if the transaction shows its queries`() {
        assertTrue(transactionHeaderBetween(null, query("1", "tx"), inverted = false, folding = TransactionFolding())!!.isExpanded)
        assertFalse(transactionHeaderBetween(null, query("1", "tx"), inverted = false, folding = TransactionFolding(toggled = setOf("tx")))!!.isExpanded)
    }

    @Test
    fun `the headers of a transaction interrupted by another event have different keys`() {
        val first = transactionHeaderBetween(null, query("1", "tx"), inverted = false, folding = TransactionFolding())!!
        val second = transactionHeaderBetween(query("0"), query("2", "tx"), inverted = false, folding = TransactionFolding())!!

        assertNotEquals(first.key, second.key)
    }

    @Test
    fun `the transactions are folded by default when the setting is off`() {
        val folded = TransactionFolding(expandedByDefault = false)
        assertTrue(query("1", "tx").isHiddenInTransaction(folded))
        assertFalse(transactionHeaderBetween(null, query("1", "tx"), inverted = false, folding = folded)!!.isExpanded)
        // flipped by the user
        val unfolded = folded.copy(toggled = setOf("tx"))
        assertFalse(query("1", "tx").isHiddenInTransaction(unfolded))
        assertTrue(transactionHeaderBetween(null, query("1", "tx"), inverted = false, folding = unfolded)!!.isExpanded)
    }

    @Test
    fun `the queries of a folded transaction are hidden`() {
        assertFalse(query("1", "tx").isHiddenInTransaction(folding = TransactionFolding()))
        assertTrue(query("1", "tx").isHiddenInTransaction(folding = TransactionFolding(toggled = setOf("tx"))))
    }

    @Test
    fun `the statements that open and close a transaction are always hidden`() {
        assertTrue(query("1", "tx", isControl = true).isHiddenInTransaction(folding = TransactionFolding(toggled = setOf("tx"))))
    }

    @Test
    fun `the events outside of a transaction are never hidden`() {
        assertFalse(query("1").isHiddenInTransaction(folding = TransactionFolding()))
        assertFalse(TimelineItemUiModel.DayHeader(day = "20/10/2026", key = "day").isHiddenInTransaction(folding = TransactionFolding()))
    }
}
