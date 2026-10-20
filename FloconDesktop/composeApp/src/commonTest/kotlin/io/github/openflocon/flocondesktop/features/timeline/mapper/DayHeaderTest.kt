package io.github.openflocon.flocondesktop.features.timeline.mapper

import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineItemUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineRowUiModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DayHeaderTest {

    private fun item(day: String) = TimelineItemUiModel.Row(
        TimelineRowUiModel(
            id = "1",
            source = TimelineSource.Network,
            day = day,
            time = "12:00:00.000",
            title = "title",
            subtitle = null,
        )
    )

    @Test
    fun `the first event gets the header of its day`() {
        assertEquals(
            TimelineItemUiModel.DayHeader("20/10/2026"),
            dayHeaderBetween(before = null, after = item("20/10/2026")),
        )
    }

    @Test
    fun `a header is inserted when the day changes`() {
        assertEquals(
            TimelineItemUiModel.DayHeader("19/10/2026"),
            dayHeaderBetween(before = item("20/10/2026"), after = item("19/10/2026")),
        )
    }

    @Test
    fun `no header between two events of the same day`() {
        assertNull(dayHeaderBetween(before = item("20/10/2026"), after = item("20/10/2026")))
    }

    @Test
    fun `no header at the end of the list or in an empty list`() {
        assertNull(dayHeaderBetween(before = item("20/10/2026"), after = null))
        assertNull(dayHeaderBetween(before = null, after = null))
    }
}
