package io.github.openflocon.flocondesktop.features.timeline.mapper

import io.github.openflocon.domain.timeline.models.TimelineEventDomainModel
import io.github.openflocon.domain.timeline.models.TimelineFacet
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineExcludeUiModel
import kotlin.test.Test
import kotlin.test.assertEquals

class ExcludesTest {

    private fun event(source: TimelineSource, facets: Map<TimelineFacet, String> = emptyMap()) = TimelineEventDomainModel(
        id = "1",
        source = source,
        timestamp = 0L,
        title = "title",
        subtitle = null,
        appInstance = null,
        facets = facets,
    )

    @Test
    fun `a network call can be excluded by its method, status and host, in the order of the facets`() {
        val call = event(
            TimelineSource.Network,
            mapOf(
                TimelineFacet.NetworkHost to "api.acme.io",
                TimelineFacet.NetworkMethod to "GET",
                TimelineFacet.NetworkStatus to "4xx",
            ),
        )

        assertEquals(
            listOf(
                TimelineExcludeUiModel(TimelineFacet.NetworkMethod, "GET"),
                TimelineExcludeUiModel(TimelineFacet.NetworkStatus, "4xx"),
                TimelineExcludeUiModel(TimelineFacet.NetworkHost, "api.acme.io"),
            ),
            call.toUi().excludes,
        )
    }

    @Test
    fun `the duration, the state and the content of a call are not offered`() {
        val call = event(
            TimelineSource.Network,
            mapOf(
                TimelineFacet.NetworkSpeed to "100",
                TimelineFacet.NetworkState to "SUCCESS",
                TimelineFacet.NetworkContent to "IMAGE",
            ),
        )

        assertEquals(emptyList(), call.toUi().excludes)
    }

    @Test
    fun `a database log can be excluded by its database and its statement`() {
        val log = event(
            TimelineSource.Database,
            mapOf(TimelineFacet.DatabaseName to "app.db", TimelineFacet.DatabaseStatement to "SELECT"),
        )

        assertEquals(listOf("app.db", "SELECT"), log.toUi().excludes.map { it.value })
    }

    @Test
    fun `a blank value is not offered, and a source without facet has nothing to exclude but itself`() {
        assertEquals(emptyList(), event(TimelineSource.Crash, mapOf(TimelineFacet.CrashException to " ")).toUi().excludes)
        assertEquals(emptyList(), event(TimelineSource.Adb).toUi().excludes)
    }
}
