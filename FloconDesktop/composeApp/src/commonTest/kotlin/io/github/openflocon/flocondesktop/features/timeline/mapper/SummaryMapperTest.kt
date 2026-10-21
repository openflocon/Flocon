package io.github.openflocon.flocondesktop.features.timeline.mapper

import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.domain.timeline.models.TimelineSourceSummaryDomainModel
import io.github.openflocon.domain.timeline.models.TimelineSummaryDomainModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SummaryMapperTest {

    private val summary = TimelineSummaryDomainModel(
        sources = mapOf(
            TimelineSource.Network to TimelineSourceSummaryDomainModel(count = 4, httpErrorCount = 2, firstTimestamp = 1_000L, lastTimestamp = 3_000L),
            TimelineSource.Crash to TimelineSourceSummaryDomainModel(count = 1, httpErrorCount = 0, firstTimestamp = 3_500L, lastTimestamp = 3_500L),
            TimelineSource.Analytics to TimelineSourceSummaryDomainModel(count = 2, httpErrorCount = 0, firstTimestamp = 500L, lastTimestamp = 2_000L),
            TimelineSource.AppStart to TimelineSourceSummaryDomainModel(count = 2, httpErrorCount = 0, firstTimestamp = 400L, lastTimestamp = 3_000L),
        ),
        sessionEventCounts = mapOf(3_000L to 6),
    )

    @Test
    fun `every filter shows its count, selected or not`() {
        val ui = summary.toUi(selected = setOf(TimelineSource.Network))

        assertEquals(4, ui.sourceCounts[TimelineSource.Network])
        assertEquals(1, ui.sourceCounts[TimelineSource.Crash])
        assertEquals(2, ui.sourceCounts[TimelineSource.AppStart])
    }

    @Test
    fun `the counters only count what the selected filters display`() {
        val ui = summary.toUi(selected = setOf(TimelineSource.Network, TimelineSource.Crash))

        assertEquals(5, ui.eventCount)
        assertEquals(2, ui.httpErrorCount)
        assertEquals(1, ui.crashCount)
    }

    @Test
    fun `a hidden category is not counted`() {
        val ui = summary.toUi(selected = setOf(TimelineSource.Network))

        assertEquals(4, ui.eventCount)
        assertEquals(0, ui.crashCount)
    }

    @Test
    fun `the accessibility events are counted like the other events`() {
        val withAccessibility = summary.copy(
            sources = summary.sources + (
                TimelineSource.Accessibility to TimelineSourceSummaryDomainModel(count = 3, httpErrorCount = 0, firstTimestamp = 600L, lastTimestamp = 2_500L)
            ),
        )

        val ui = withAccessibility.toUi(selected = setOf(TimelineSource.Network, TimelineSource.Accessibility))

        assertEquals(3, ui.sourceCounts[TimelineSource.Accessibility])
        assertEquals(7, ui.eventCount)
    }

    @Test
    fun `the app starts are sessions, not events`() {
        val ui = summary.toUi(selected = TimelineSource.entries.toSet())

        assertEquals(7, ui.eventCount)
        assertEquals(2, ui.sessionCount)
    }

    @Test
    fun `the range covers the displayed events only`() {
        val all = summary.toUi(selected = TimelineSource.entries.toSet())
        val networkOnly = summary.toUi(selected = setOf(TimelineSource.Network))

        assertEquals(false, all.range == networkOnly.range)
        assertNull(summary.toUi(selected = emptySet()).range)
    }

    @Test
    fun `the session counts are kept`() {
        assertEquals(mapOf(3_000L to 6), summary.toUi(selected = TimelineSource.entries.toSet()).sessionEventCounts)
    }
}
