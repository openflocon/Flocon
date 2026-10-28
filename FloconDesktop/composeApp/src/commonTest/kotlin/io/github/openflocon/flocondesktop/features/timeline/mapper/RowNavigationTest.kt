package io.github.openflocon.flocondesktop.features.timeline.mapper

import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.flocondesktop.features.timeline.model.previewTimelineRowUiModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class RowNavigationTest {

    // header, call a (response + request), call b, header, call c
    private val keys = listOf(null, "a", "a", "b", null, "c")

    private fun next(selected: String?, step: Int) = adjacentCallIndex(keys.size, selected, step) { keys[it] }

    @Test
    fun `without selection the first call is selected`() {
        assertEquals(1, next(selected = null, step = 1))
        assertEquals(1, next(selected = null, step = -1))
    }

    @Test
    fun `moving skips the other line of the call and the headers`() {
        assertEquals(3, next(selected = "a", step = 1))
        assertEquals(5, next(selected = "b", step = 1))
        assertEquals(3, next(selected = "c", step = -1))
        assertEquals(2, next(selected = "b", step = -1))
    }

    @Test
    fun `nothing happens at the ends of the list`() {
        assertNull(next(selected = "c", step = 1))
        assertNull(next(selected = "a", step = -1))
    }

    @Test
    fun `a selection not in the list anymore starts again from the top`() {
        assertEquals(1, next(selected = "gone", step = 1))
    }

    @Test
    fun `the copy of an event is its url, its query or its crash message`() {
        assertEquals("https://acme.io/a", previewTimelineRowUiModel(TimelineSource.Network, title = "https://acme.io/a").copyText())
        assertEquals(
            "java.lang.Boom: oops",
            previewTimelineRowUiModel(TimelineSource.Crash, title = "java.lang.Boom", subtitle = "oops").copyText(),
        )
        assertNull(previewTimelineRowUiModel(TimelineSource.AppStart, title = "").copyText())
    }

    @Test
    fun `only the slow calls get a level`() {
        assertEquals(DurationLevel.Normal, durationLevel(null))
        assertEquals(DurationLevel.Normal, durationLevel(999.0))
        assertEquals(DurationLevel.Slow, durationLevel(1_000.0))
        assertEquals(DurationLevel.VerySlow, durationLevel(3_000.0))
    }
}
