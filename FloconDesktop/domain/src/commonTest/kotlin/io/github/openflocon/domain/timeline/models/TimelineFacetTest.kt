package io.github.openflocon.domain.timeline.models

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TimelineFacetTest {

    @Test
    fun `the reserved log types are the known values of the log type facet`() {
        assertEquals(listOf("navigation", "userAction", "lifecycle"), TIMELINE_RESERVED_LOG_TYPES)
        assertEquals(TIMELINE_RESERVED_LOG_TYPES, TimelineFacet.LogType.knownValues)
    }

    @Test
    fun `the tagged level facet belongs to the logs and knows the four levels`() {
        assertEquals(TimelineSource.Log, TimelineFacet.LogTaggedLevel.source)
        assertEquals(listOf("DEBUG", "INFO", "WARNING", "ERROR"), TimelineFacet.LogTaggedLevel.knownValues)
    }

    @Test
    fun `the log filters keep everything by default`() {
        val filter = TimelineFilterDomainModel()
        assertTrue(filter.logTaggedEnabled)
        assertTrue(filter.logTagRules.isEmpty())
    }
}
