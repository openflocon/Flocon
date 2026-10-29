package io.github.openflocon.flocondesktop.features.timeline.mapper

import io.github.openflocon.domain.timeline.models.TimelineFacet
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineSummaryUiModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FilterPanelMapperTest {

    private val all = TimelineSource.entries.toSet()

    private val summary = TimelineSummaryUiModel.Empty.copy(
        sourceCounts = mapOf(
            TimelineSource.Adb to 2,
            TimelineSource.Network to 3,
            TimelineSource.Database to 0,
        ),
        facetCounts = mapOf(
            TimelineFacet.NetworkMethod to mapOf("POST" to 1, "GET" to 2),
            TimelineFacet.NetworkStatus to mapOf("2xx" to 3, "5xx" to 0),
        ),
    )

    @Test
    fun `the sources with events come first, in their order`() {
        val panel = summary.toFilterPanel(all, emptyMap(), onlyWithEvents = false)

        assertEquals(
            listOf(TimelineSource.Network, TimelineSource.Adb),
            panel.sections.take(2).map { it.source },
        )
        assertEquals(TimelineSource.entries.size, panel.allCount)
        assertEquals(2, panel.withEventsCount)
    }

    @Test
    fun `every known value of a facet is listed, with its count`() {
        val network = summary.toFilterPanel(all, emptyMap(), onlyWithEvents = false).sections.first { it.source == TimelineSource.Network }

        val methods = network.groups.first { it.facet == TimelineFacet.NetworkMethod }.values
        assertEquals(TimelineFacet.NetworkMethod.knownValues, methods.map { it.value })
        assertEquals(listOf("GET" to 2, "POST" to 1, "PUT" to 0), methods.take(3).map { it.value to it.count })
    }

    @Test
    fun `the values brought by the events come after the known ones, sorted`() {
        val withDatabases = summary.copy(
            facetCounts = summary.facetCounts + (TimelineFacet.DatabaseName to mapOf("b.db" to 1, "a.db" to 3)),
            sourceCounts = summary.sourceCounts + (TimelineSource.Database to 4),
        )

        val database = withDatabases.toFilterPanel(all, emptyMap(), onlyWithEvents = false).sections.first { it.source == TimelineSource.Database }

        assertEquals(listOf("a.db", "b.db"), database.groups.first { it.facet == TimelineFacet.DatabaseName }.values.map { it.value })
    }

    @Test
    fun `a facet without known value is not listed before an event brings one`() {
        val database = summary.toFilterPanel(all, emptyMap(), onlyWithEvents = false).sections.first { it.source == TimelineSource.Database }

        assertTrue(database.groups.none { it.facet == TimelineFacet.DatabaseName })
        // the statements are always known
        assertEquals(
            listOf(TimelineFacet.DatabaseStatement),
            database.groups.map { it.facet },
        )
    }

    @Test
    fun `only the sources and values with events are kept on demand`() {
        val panel = summary.toFilterPanel(all, emptyMap(), onlyWithEvents = true)

        assertEquals(listOf(TimelineSource.Network, TimelineSource.Adb), panel.sections.map { it.source })
        val status = panel.sections.first().groups.first { it.facet == TimelineFacet.NetworkStatus }.values
        assertEquals(listOf("2xx"), status.map { it.value })
    }

    @Test
    fun `a value filtered on stays even without event, to be undone`() {
        val panel = summary.toFilterPanel(all, mapOf(TimelineFacet.NetworkStatus to setOf("5xx")), onlyWithEvents = true)

        val status = panel.sections.first().groups.first { it.facet == TimelineFacet.NetworkStatus }.values
        assertEquals(listOf("2xx", "5xx"), status.map { it.value })
        // every value is checked unless it was unchecked
        assertTrue(status.first().selected)
        assertFalse(status.last().selected)
    }

    @Test
    fun `a source filtered on stays even without event`() {
        val panel = summary.toFilterPanel(all, mapOf(TimelineFacet.DatabaseName to setOf("app.db")), onlyWithEvents = true)

        assertTrue(panel.sections.any { it.source == TimelineSource.Database })
    }

    @Test
    fun `the current session only is an active filter`() {
        val panel = summary.toFilterPanel(all, emptyMap(), onlyWithEvents = false, currentSessionOnly = true)

        assertTrue(panel.currentSessionOnly)
        assertTrue(panel.hasActiveFilters)
        assertEquals(1, panel.activeFilterCount)
    }

    @Test
    fun `the active filters are the facets selected and the sources hidden`() {
        val none = summary.toFilterPanel(all, emptyMap(), onlyWithEvents = false)
        assertFalse(none.hasActiveFilters)

        val some = summary.toFilterPanel(
            selectedSources = all - TimelineSource.Crash,
            facets = mapOf(TimelineFacet.NetworkMethod to setOf("GET", "POST")),
            onlyWithEvents = false,
        )
        assertTrue(some.hasActiveFilters)
        assertEquals(3, some.activeFilterCount)
        assertEquals(2, some.sections.first { it.source == TimelineSource.Network }.activeFacetCount)
        assertFalse(some.sections.first { it.source == TimelineSource.Crash }.enabled)
    }
}
