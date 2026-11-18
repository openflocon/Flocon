package io.github.openflocon.flocondesktop.features.timeline.mapper

import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.domain.timeline.screenshot.TimelineScreenshotDomainModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineItemUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineRowUiModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class ScreenshotMarkerTest {

    private fun shot(timestamp: Long) = TimelineScreenshotDomainModel(timestamp, "/tmp/$timestamp.jpg", "device", "com.app")

    private fun row(timestamp: Long, day: String = "20/10/2026") = TimelineItemUiModel.Row(
        TimelineRowUiModel(
            id = "$timestamp",
            source = TimelineSource.Network,
            day = day,
            time = "12:00:00.000",
            timestamp = timestamp,
            title = "title",
            subtitle = null,
        ),
    )

    private val shots = listOf(shot(100), shot(200), shot(300))

    @Test
    fun `the latest capture between two events is returned`() {
        assertEquals(200L, screenshotBetween(shots, newerTimestamp = 250, olderTimestamp = 150)?.timestamp)
    }

    @Test
    fun `several captures in one gap give only the latest`() {
        assertEquals(300L, screenshotBetween(shots, newerTimestamp = 1_000, olderTimestamp = 50)?.timestamp)
    }

    @Test
    fun `a capture after the newest event is returned for the top of the list`() {
        assertEquals(300L, screenshotBetween(shots, newerTimestamp = null, olderTimestamp = 250)?.timestamp)
    }

    @Test
    fun `a capture before the oldest event is returned for the bottom of the list`() {
        assertEquals(100L, screenshotBetween(shots, newerTimestamp = 150, olderTimestamp = null)?.timestamp)
    }

    @Test
    fun `no capture in the gap gives null`() {
        assertNull(screenshotBetween(shots, newerTimestamp = 190, olderTimestamp = 150))
        assertNull(screenshotBetween(emptyList(), newerTimestamp = 190, olderTimestamp = 150))
    }

    @Test
    fun `no events at all means no marker`() {
        assertNull(screenshotBetween(shots, newerTimestamp = null, olderTimestamp = null))
    }

    @Test
    fun `an event at the exact time of a capture is above the capture, the capture belongs to the older gap`() {
        // newer is exclusive, older is inclusive: the capture is not counted twice between two gaps
        assertNull(screenshotBetween(shots, newerTimestamp = 200, olderTimestamp = 201))
        assertEquals(200L, screenshotBetween(shots, newerTimestamp = 300, olderTimestamp = 200)?.timestamp)
        assertEquals(100L, screenshotBetween(shots, newerTimestamp = 200, olderTimestamp = 100)?.timestamp)
    }

    @Test
    fun `the marker between two rows carries the capture, its day and a unique key`() {
        val marker = assertIs<TimelineItemUiModel.Screenshot>(screenshotMarkerBetween(before = row(250), after = row(150), shots = shots))

        assertEquals(200L, marker.timestamp)
        assertEquals("/tmp/200.jpg", marker.filePath)
        assertEquals("screenshot:200", marker.key)
    }

    @Test
    fun `an empty list gets no marker`() {
        assertNull(screenshotMarkerBetween(before = null, after = null, shots = shots))
    }

    @Test
    fun `without captures there is never a marker`() {
        assertNull(screenshotMarkerBetween(before = row(250), after = row(150), shots = emptyList()))
    }

    @Test
    fun `no day header between a marker and an event of the same day`() {
        val marker = TimelineItemUiModel.Screenshot(timestamp = 200, day = "20/10/2026", filePath = "/tmp/a.jpg", key = "screenshot:200")

        assertNull(separatorBetween(before = marker, after = row(150, day = "20/10/2026")))
        assertNull(separatorBetween(before = row(250, day = "20/10/2026"), after = marker))
    }

    @Test
    fun `a day header is inserted between a marker and an event of another day`() {
        val marker = TimelineItemUiModel.Screenshot(timestamp = 200, day = "20/10/2026", filePath = "/tmp/a.jpg", key = "screenshot:200")

        val header = assertIs<TimelineItemUiModel.DayHeader>(separatorBetween(before = marker, after = row(150, day = "19/10/2026")))

        assertEquals("19/10/2026", header.day)
    }
}
