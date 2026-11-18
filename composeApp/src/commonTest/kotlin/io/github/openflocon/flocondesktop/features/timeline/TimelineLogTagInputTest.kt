package io.github.openflocon.flocondesktop.features.timeline

import io.github.openflocon.domain.timeline.models.TimelineFacet
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.flocondesktop.features.timeline.mapper.toFilterPanel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineSummaryUiModel
import kotlin.test.Test
import kotlin.test.assertEquals

class TimelineLogTagInputTest {

    private val input = TimelineLogTagInput()

    @Test
    fun `the field reads each typed character at once, without waiting for any flow`() {
        input.set("t")
        assertEquals("t", input.text.value)
        input.set("te")
        assertEquals("te", input.text.value)
        assertEquals("te", input.query.value)
    }

    @Test
    fun `clearing empties the field and the query`() {
        input.set("billing")

        input.clear()

        assertEquals("", input.text.value)
        assertEquals("", input.query.value)
    }

    @Test
    fun `the suggestions follow the query while the field keeps its text`() {
        val summary = TimelineSummaryUiModel.Empty.copy(
            sourceCounts = mapOf(TimelineSource.Log to 3),
            facetCounts = mapOf(TimelineFacet.LogType to mapOf("billing" to 2, "auth" to 1)),
        )

        input.set("bi")
        val narrowed = summary.toFilterPanel(TimelineSource.entries.toSet(), emptyMap(), logTagQuery = input.query.value).logs

        assertEquals(listOf("billing"), narrowed.tagSuggestions.map { it.value })
        assertEquals("bi", input.text.value)
    }
}
