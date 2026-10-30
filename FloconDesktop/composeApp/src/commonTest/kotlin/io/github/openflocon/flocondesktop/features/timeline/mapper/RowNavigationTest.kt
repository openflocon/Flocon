package io.github.openflocon.flocondesktop.features.timeline.mapper

import io.github.openflocon.domain.timeline.models.TimelinePhase
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.flocondesktop.features.timeline.model.previewTimelineRowUiModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
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

    @Test
    fun `the same event repeated is linked, the others are not`() {
        val name = "screen_view"
        assertEquals(
            previewTimelineRowUiModel(TimelineSource.Analytics, id = "1", title = name).linkKey(),
            previewTimelineRowUiModel(TimelineSource.Analytics, id = "2", title = name).linkKey(),
        )
        assertNotEquals(
            previewTimelineRowUiModel(TimelineSource.Analytics, title = name).linkKey(),
            previewTimelineRowUiModel(TimelineSource.Deeplink, title = name).linkKey(),
        )
        assertNull(previewTimelineRowUiModel(TimelineSource.Database, title = "SELECT 1").linkKey())
        assertNull(previewTimelineRowUiModel(TimelineSource.Network, title = "https://acme.io").linkKey())
        assertNull(previewTimelineRowUiModel(TimelineSource.AppStart, title = "").linkKey())
        assertNull(previewTimelineRowUiModel(TimelineSource.WebView, title = "log from the page").linkKey())
    }

    private val linkRows = listOf(
        previewTimelineRowUiModel(TimelineSource.Database, id = "1", title = "SELECT 1"),
        previewTimelineRowUiModel(TimelineSource.Analytics, id = "2", title = "screen_view"),
        previewTimelineRowUiModel(TimelineSource.Network, id = "n", title = "https://acme.io", phase = TimelinePhase.Response),
        previewTimelineRowUiModel(TimelineSource.Analytics, id = "3", title = "screen_view"),
        previewTimelineRowUiModel(TimelineSource.Network, id = "n", title = "https://acme.io"),
        previewTimelineRowUiModel(TimelineSource.AppStart, id = "s", title = ""),
    )

    private fun linked(callKey: String?, linkKey: String?) = linkedRowIndices(linkRows.size, callKey, linkKey) { linkRows[it] }

    @Test
    fun `nothing is linked without a key, the rows with nothing to link do not match each other`() {
        assertEquals(emptyList(), linked(callKey = null, linkKey = null))
    }

    @Test
    fun `the two lines of a call are linked`() {
        assertEquals(listOf(2, 4), linked(callKey = "Network:n", linkKey = null))
    }

    @Test
    fun `the same event repeated is linked`() {
        assertEquals(listOf(1, 3), linked(callKey = null, linkKey = "Analytics:screen_view"))
    }
}
