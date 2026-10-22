package io.github.openflocon.flocondesktop.features.timeline.mapper

import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineItemUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineRowUiModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertNull

class SeparatorTest {

    private fun item(day: String, id: String = "1") = TimelineItemUiModel.Row(
        TimelineRowUiModel(
            id = id,
            source = TimelineSource.Network,
            day = day,
            time = "12:00:00.000",
            timestamp = 0L,
            title = "title",
            subtitle = null,
        )
    )

    private fun dayOf(header: TimelineItemUiModel?) = assertIs<TimelineItemUiModel.DayHeader>(header).day

    @Test
    fun `the first event gets the header of its day`() {
        assertEquals("20/10/2026", dayOf(separatorBetween(before = null, after = item("20/10/2026"))))
    }

    @Test
    fun `a header is inserted when the day changes`() {
        assertEquals(
            "19/10/2026",
            dayOf(separatorBetween(before = item("20/10/2026"), after = item("19/10/2026"))),
        )
    }

    @Test
    fun `no header between two events of the same day`() {
        assertNull(separatorBetween(before = item("20/10/2026"), after = item("20/10/2026")))
    }

    @Test
    fun `no header at the end of the list or in an empty list`() {
        assertNull(separatorBetween(before = item("20/10/2026"), after = null))
        assertNull(separatorBetween(before = null, after = null))
    }

    @Test
    fun `the key of a header depends on the event below, to stay unique`() {
        val first = separatorBetween(before = null, after = item("20/10/2026", id = "1"))!!
        val second = separatorBetween(before = null, after = item("20/10/2026", id = "2"))!!

        assertNotEquals(first.key, second.key)
    }
}
