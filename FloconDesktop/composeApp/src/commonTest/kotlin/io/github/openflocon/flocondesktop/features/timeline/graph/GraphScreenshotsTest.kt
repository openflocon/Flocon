package io.github.openflocon.flocondesktop.features.timeline.graph

import io.github.openflocon.domain.timeline.screenshot.TimelineScreenshotDomainModel
import kotlin.test.Test
import kotlin.test.assertEquals

class GraphScreenshotsTest {

    private fun shot(timestamp: Long) = TimelineScreenshotDomainModel(timestamp, "/tmp/$timestamp.jpg", "device", "com.app")

    private fun ui(timeMs: Long) = GraphScreenshotUiModel(timeMs, "/tmp/$timeMs.jpg")

    // 1 px per ms from 0
    private val viewport = GraphViewport(startMs = 0.0, pxPerMs = 1.0)

    @Test
    fun `only the screenshots of the session are kept`() {
        val shots = listOf(shot(900), shot(1_000), shot(1_500), shot(2_000), shot(2_100))

        val kept = screenshotsOfSession(shots, sessionStart = 1_000, nextSessionStart = 2_000)

        assertEquals(listOf(0L, 500L), kept.map { it.timeMs })
    }

    @Test
    fun `the latest session has no end`() {
        val kept = screenshotsOfSession(listOf(shot(1_000), shot(9_000)), sessionStart = 1_000, nextSessionStart = null)

        assertEquals(listOf(0L, 8_000L), kept.map { it.timeMs })
    }

    @Test
    fun `timeMs is relative to the session start and the order is kept ascending`() {
        val kept = screenshotsOfSession(listOf(shot(1_300), shot(1_100)), sessionStart = 1_000, nextSessionStart = null)

        assertEquals(listOf(100L, 300L), kept.map { it.timeMs })
        assertEquals("/tmp/1100.jpg", kept.first().filePath)
    }

    @Test
    fun `a screenshot before the session start is dropped`() {
        assertEquals(emptyList(), screenshotsOfSession(listOf(shot(10)), sessionStart = 1_000, nextSessionStart = null))
    }

    @Test
    fun `an empty list gives an empty strip`() {
        assertEquals(emptyList(), screenshotsOfSession(emptyList(), sessionStart = 0, nextSessionStart = null))
        assertEquals(emptyList(), thinScreenshots(emptyList(), viewport, minGapPx = 60f))
    }

    @Test
    fun `thinning keeps all when there is room`() {
        val shots = listOf(ui(0), ui(100), ui(200))

        assertEquals(shots, thinScreenshots(shots, viewport, minGapPx = 100f))
    }

    @Test
    fun `thinning drops the ones that would overlap and keeps the first`() {
        val shots = listOf(ui(0), ui(30), ui(60), ui(90), ui(120))

        // at least 60 px between two thumbnails: 0, 60, 120
        assertEquals(listOf(0L, 60L, 120L), thinScreenshots(shots, viewport, minGapPx = 60f).map { it.timeMs })
    }

    @Test
    fun `zooming in brings the dropped ones back`() {
        val shots = listOf(ui(0), ui(30), ui(60))

        assertEquals(2, thinScreenshots(shots, viewport, minGapPx = 60f).size)
        assertEquals(3, thinScreenshots(shots, GraphViewport(startMs = 0.0, pxPerMs = 4.0), minGapPx = 60f).size)
    }

    @Test
    fun `the time of a screenshot is minutes and seconds since the start of the session`() {
        assertEquals("0:00", formatScreenshotTime(0))
        assertEquals("0:30", formatScreenshotTime(30_999))
        assertEquals("1:05", formatScreenshotTime(65_000))
        assertEquals("61:01", formatScreenshotTime(3_661_000))
    }
}
