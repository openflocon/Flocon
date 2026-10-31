package io.github.openflocon.domain.timeline.models

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TimelineStatusRulesTest {

    @Test
    fun `a code is a range of one`() {
        assertEquals(TimelineStatusMatcher(404, 404), TimelineStatusMatcher.parse("404"))
    }

    @Test
    fun `a range keeps both ends`() {
        assertEquals(TimelineStatusMatcher(500, 503), TimelineStatusMatcher.parse(" 500 - 503 "))
    }

    @Test
    fun `a reversed range or a word is not a status`() {
        assertNull(TimelineStatusMatcher.parse("503-500"))
        assertNull(TimelineStatusMatcher.parse("abc"))
        assertNull(TimelineStatusMatcher.parse("1-2-3"))
    }

    @Test
    fun `codes and ranges are split on commas and spaces`() {
        assertEquals(
            listOf(TimelineStatusMatcher(404, 404), TimelineStatusMatcher(500, 503)),
            parseTimelineStatusRules("404, 500-503"),
        )
        assertEquals(
            listOf(TimelineStatusMatcher(500, 503)),
            parseTimelineStatusRules("500 - 503"),
        )
    }

    @Test
    fun `one invalid part rejects the whole text`() {
        assertNull(parseTimelineStatusRules("404, foo"))
        assertNull(parseTimelineStatusRules("  "))
    }
}
