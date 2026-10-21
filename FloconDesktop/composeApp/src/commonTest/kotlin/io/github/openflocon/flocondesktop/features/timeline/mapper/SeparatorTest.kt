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

    private fun item(day: String, id: String = "1", session: Long? = null) = TimelineItemUiModel.Row(
        TimelineRowUiModel(
            id = id,
            source = TimelineSource.Network,
            day = day,
            time = "12:00:00.000",
            timestamp = 0L,
            title = "title",
            subtitle = null,
            sessionStart = session,
        )
    )

    private fun dayOf(header: TimelineItemUiModel?) = assertIs<TimelineItemUiModel.DayHeader>(header).day
    private fun sessionOf(header: TimelineItemUiModel?) = assertIs<TimelineItemUiModel.SessionHeader>(header).sessionStart

    // --- days

    @Test
    fun `the first event gets the header of its day`() {
        assertEquals("20/10/2026", dayOf(separatorBetween(before = null, after = item("20/10/2026"), showSessions = false)))
    }

    @Test
    fun `a header is inserted when the day changes`() {
        assertEquals(
            "19/10/2026",
            dayOf(separatorBetween(before = item("20/10/2026"), after = item("19/10/2026"), showSessions = false)),
        )
    }

    @Test
    fun `no header between two events of the same day`() {
        assertNull(separatorBetween(before = item("20/10/2026"), after = item("20/10/2026"), showSessions = false))
    }

    @Test
    fun `no header at the end of the list or in an empty list`() {
        assertNull(separatorBetween(before = item("20/10/2026"), after = null, showSessions = true))
        assertNull(separatorBetween(before = null, after = null, showSessions = true))
    }

    @Test
    fun `the key of a header depends on the event below, to stay unique`() {
        val first = separatorBetween(before = null, after = item("20/10/2026", id = "1"), showSessions = false)!!
        val second = separatorBetween(before = null, after = item("20/10/2026", id = "2"), showSessions = false)!!

        assertNotEquals(first.key, second.key)
    }

    // --- sessions

    @Test
    fun `the first event of a session gets the header of its session`() {
        assertEquals(1000L, sessionOf(separatorBetween(before = null, after = item("20/10/2026", session = 1000L), showSessions = true)))
    }

    @Test
    fun `a session header is inserted when the session changes`() {
        val newer = item("20/10/2026", id = "2", session = 3000L)
        val older = item("20/10/2026", id = "1", session = 1000L)

        assertEquals(1000L, sessionOf(separatorBetween(before = newer, after = older, showSessions = true)))
    }

    @Test
    fun `no header between two events of the same session`() {
        val newer = item("20/10/2026", id = "2", session = 1000L)
        val older = item("20/10/2026", id = "1", session = 1000L)

        assertNull(separatorBetween(before = newer, after = older, showSessions = true))
    }

    @Test
    fun `a session header holds the day, so it replaces the day header`() {
        val newer = item("20/10/2026", id = "2", session = 3000L)
        val older = item("19/10/2026", id = "1", session = 1000L)

        assertEquals(1000L, sessionOf(separatorBetween(before = newer, after = older, showSessions = true)))
    }

    @Test
    fun `the day still changes inside a session`() {
        val newer = item("20/10/2026", id = "2", session = 1000L)
        val older = item("19/10/2026", id = "1", session = 1000L)

        assertEquals("19/10/2026", dayOf(separatorBetween(before = newer, after = older, showSessions = true)))
    }

    @Test
    fun `without the app start filter the events are only grouped by day`() {
        assertEquals(
            "20/10/2026",
            dayOf(separatorBetween(before = null, after = item("20/10/2026", session = 1000L), showSessions = false)),
        )
    }

    @Test
    fun `events before the first known session only have their day`() {
        val newer = item("20/10/2026", id = "2", session = 1000L)
        val older = item("20/10/2026", id = "1", session = null)

        assertNull(separatorBetween(before = newer, after = older, showSessions = true))
        assertEquals("20/10/2026", dayOf(separatorBetween(before = null, after = older, showSessions = true)))
    }

    @Test
    fun `the key of a session header depends on the event below, to stay unique`() {
        val first = separatorBetween(before = null, after = item("20/10/2026", id = "1", session = 1000L), showSessions = true)!!
        val second = separatorBetween(before = null, after = item("20/10/2026", id = "2", session = 1000L), showSessions = true)!!

        assertNotEquals(first.key, second.key)
    }
}
