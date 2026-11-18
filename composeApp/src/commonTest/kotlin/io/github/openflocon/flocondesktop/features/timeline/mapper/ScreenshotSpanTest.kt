package io.github.openflocon.flocondesktop.features.timeline.mapper

import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineItemUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineRowUiModel
import kotlin.test.Test
import kotlin.test.assertEquals

class ScreenshotSpanTest {

    private fun row(id: Int) = TimelineItemUiModel.Row(
        TimelineRowUiModel(
            id = "$id",
            source = TimelineSource.Network,
            day = "20/10/2026",
            time = "12:00:00.000",
            timestamp = id.toLong(),
            title = "title",
            subtitle = null,
        ),
    )

    private fun shot(id: Int) = TimelineItemUiModel.Screenshot(timestamp = id.toLong(), day = "20/10/2026", filePath = "/tmp/$id.jpg", key = "screenshot:$id")

    private fun header() = TimelineItemUiModel.DayHeader(day = "20/10/2026", key = "day")

    @Test
    fun `a screenshot covers the events above it, up to the next screenshot`() {
        // newest first: [4, 3, shot, 2, 1, shot]
        val items = listOf(row(4), row(3), shot(30), row(2), row(1), shot(10))
        assertEquals(listOf(5, 3), screenshotSpanIndices(5, items::getOrNull))
        assertEquals(listOf(2, 0), screenshotSpanIndices(2, items::getOrNull))
    }

    @Test
    fun `the newest screenshot covers up to the top of the list`() {
        val items = listOf(row(2), row(1), shot(10))
        assertEquals(listOf(2, 0), screenshotSpanIndices(2, items::getOrNull))
    }

    @Test
    fun `a screenshot with no event above it is alone`() {
        val items = listOf(shot(20), row(1))
        assertEquals(listOf(0), screenshotSpanIndices(0, items::getOrNull))
    }

    @Test
    fun `headers and items not loaded do not end the span`() {
        val items = listOf(row(3), header(), row(2), shot(10))
        assertEquals(listOf(3, 0), screenshotSpanIndices(3, items::getOrNull))
        assertEquals(listOf(1), screenshotSpanIndices(1, listOf<TimelineItemUiModel?>(null, shot(10))::getOrNull))
    }

    private fun navigation(id: Int, title: String) = TimelineItemUiModel.Row(
        TimelineRowUiModel(
            id = "$id",
            source = TimelineSource.Log,
            day = "20/10/2026",
            time = "12:00:00.000",
            timestamp = id.toLong(),
            title = title,
            subtitle = "navigation",
        ),
    )

    @Test
    fun `the screen is the last navigation before the screenshot`() {
        // newest first: [shot, 3, nav Detail, 2, nav Feed, 1]
        val items = listOf(shot(30), row(3), navigation(20, "Detail"), row(2), navigation(10, "Feed"), row(1))
        assertEquals("Detail", screenshotScreenName(0, items.size, items::getOrNull))
        assertEquals("Feed", screenshotScreenName(3, items.size, items::getOrNull))
    }

    @Test
    fun `no screen without a navigation below the screenshot`() {
        val items = listOf(navigation(20, "Detail"), shot(10), row(1))
        assertEquals(null, screenshotScreenName(1, items.size, items::getOrNull))
    }
}
