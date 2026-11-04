package io.github.openflocon.flocondesktop.features.timeline.mapper

import io.github.openflocon.domain.timeline.models.TIMELINE_DURATION_BUCKETS_MS
import io.github.openflocon.domain.timeline.models.TimelineDurationRangeDomainModel
import io.github.openflocon.domain.timeline.models.TimelineFacet
import io.github.openflocon.domain.timeline.models.TimelineRuleDomainModel
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.domain.timelinelog.models.TimelineLogLevel
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkMethodUi
import io.github.openflocon.flocondesktop.features.timeline.TimelineLogRules
import io.github.openflocon.flocondesktop.features.timeline.TimelineLogTagRule
import io.github.openflocon.flocondesktop.features.timeline.TimelineNetworkRules
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineRuleUiModel
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

    private val logSummary = summary.copy(
        sourceCounts = summary.sourceCounts + (TimelineSource.Log to 14),
        facetCounts = mapOf(
            TimelineFacet.LogType to mapOf("navigation" to 4, "userAction" to 1, "billing" to 5, "auth" to 3, "Zeta" to 5),
            TimelineFacet.LogTaggedLevel to mapOf("ERROR" to 1, "DEBUG" to 12),
        ),
    )

    @Test
    fun `the log section has no generic group, the logs card shows its filters`() {
        val log = logSummary.toFilterPanel(all, emptyMap()).sections.first { it.source == TimelineSource.Log }

        assertTrue(log.groups.isEmpty())
    }

    @Test
    fun `the log types are the reserved ones then the tagged logs, with their counts`() {
        val logs = logSummary.toFilterPanel(all, emptyMap()).logs

        assertEquals(listOf("navigation", "userAction", "lifecycle", null), logs.types.map { it.type })
        assertEquals(listOf(4, 1, 0, 13), logs.types.map { it.count })
        assertEquals(18, logs.totalCount)
        assertTrue(logs.allTypesSelected)
    }

    @Test
    fun `a reserved type hidden or the tagged logs disabled is not all the types`() {
        val hidden = logSummary.toFilterPanel(all, mapOf(TimelineFacet.LogType to setOf("navigation"))).logs
        assertFalse(hidden.allTypesSelected)
        assertEquals(listOf(false, true, true, true), hidden.types.map { it.selected })

        val disabled = logSummary.toFilterPanel(all, emptyMap(), logs = TimelineLogRules(taggedEnabled = false)).logs
        assertFalse(disabled.allTypesSelected)
        assertEquals(listOf(true, true, true, false), disabled.types.map { it.selected })
    }

    @Test
    fun `the tag suggestions are narrowed by the text, without the existing rules, the most used first`() {
        val all = logSummary.toFilterPanel(all, emptyMap()).logs.tagSuggestions
        assertEquals(listOf("billing", "Zeta", "auth"), all.map { it.value })
        assertEquals(listOf(5, 5, 3), all.map { it.count })

        val narrowed = logSummary.toFilterPanel(this.all, emptyMap(), logTagQuery = "ET").logs
        assertEquals(listOf("Zeta"), narrowed.tagSuggestions.map { it.value })
        assertEquals("ET", narrowed.tagQuery)

        val withRule = logSummary.toFilterPanel(
            this.all,
            emptyMap(),
            logs = TimelineLogRules(tagRules = listOf(TimelineLogTagRule("Billing", exclude = true))),
        ).logs
        assertEquals(listOf("Zeta", "auth"), withRule.tagSuggestions.map { it.value })
    }

    @Test
    fun `at most eight tags are suggested`() {
        val tags = (1..12).associate { "tag$it" to it }
        val many = logSummary.copy(facetCounts = mapOf(TimelineFacet.LogType to tags))

        val suggestions = many.toFilterPanel(all, emptyMap()).logs.tagSuggestions

        assertEquals(8, suggestions.size)
        assertEquals("tag12", suggestions.first().value)
    }

    @Test
    fun `the tag rules are split in includes and excludes`() {
        val logs = logSummary.toFilterPanel(
            all,
            emptyMap(),
            logs = TimelineLogRules(
                tagRules = listOf(
                    TimelineLogTagRule("a", exclude = false),
                    TimelineLogTagRule("b", exclude = true, active = false),
                ),
            ),
        ).logs

        assertEquals(listOf(TimelineRuleUiModel("a", exclude = false)), logs.tagIncludes)
        assertEquals(listOf(TimelineRuleUiModel("b", exclude = true, active = false)), logs.tagExcludes)
    }

    @Test
    fun `the levels come from the tagged levels, every one listed`() {
        val levels = logSummary.toFilterPanel(all, mapOf(TimelineFacet.LogTaggedLevel to setOf("INFO"))).logs.levels

        assertEquals(listOf("DEBUG", "INFO", "WARNING", "ERROR"), levels.map { it.value })
        assertEquals(listOf(12, 0, 0, 1), levels.map { it.count })
        assertEquals(listOf(true, false, true, true), levels.map { it.selected })
        assertEquals(listOf(TimelineLogLevel.Debug, TimelineLogLevel.Info, TimelineLogLevel.Warning, TimelineLogLevel.Error), levels.map { it.logLevel })
    }

    @Test
    fun `the logs switch and the active tag rules are active filters`() {
        val rules = TimelineLogRules(
            taggedEnabled = false,
            tagRules = listOf(TimelineLogTagRule("a", exclude = false), TimelineLogTagRule("b", exclude = true, active = false)),
        )

        val panel = logSummary.toFilterPanel(all, emptyMap(), logs = rules)

        assertEquals(2, panel.activeFilterCount)
        assertTrue(panel.hasActiveFilters)
        assertEquals(2, panel.sections.first { it.source == TimelineSource.Log }.activeFacetCount)
    }

    @Test
    fun `the sources with events come first, in their order`() {
        val panel = summary.toFilterPanel(all, emptyMap())

        assertEquals(
            listOf(TimelineSource.Network, TimelineSource.Adb),
            panel.sections.take(2).map { it.source },
        )
    }

    @Test
    fun `every known value of a facet is listed, with its count`() {
        val network = summary.toFilterPanel(all, emptyMap()).sections.first { it.source == TimelineSource.Network }

        val methods = network.groups.first { it.facet == TimelineFacet.NetworkMethod }.values
        assertEquals(TimelineFacet.NetworkMethod.knownValues, methods.map { it.value })
        assertEquals(listOf("GET" to 2, "POST" to 1, "PUT" to 0), methods.take(3).map { it.value to it.count })
    }

    @Test
    fun `the methods and the status classes are displayed like in the network screen`() {
        val network = summary.toFilterPanel(all, emptyMap()).sections.first { it.source == TimelineSource.Network }

        val methods = network.groups.first { it.facet == TimelineFacet.NetworkMethod }.values.associate { it.value to it.method }
        assertEquals(NetworkMethodUi.Http.GET, methods["GET"])
        assertEquals(NetworkMethodUi.WebSocket, methods["WS"])
        assertEquals(NetworkMethodUi.Grpc, methods["GRPC"])
        assertEquals("OPTIONS", (methods["OPTIONS"] as NetworkMethodUi.OTHER).text)

        // the methods and the status are displayed as tags side by side, a hidden value is unchecked
        val hiddenPost = summary.toFilterPanel(all, mapOf(TimelineFacet.NetworkMethod to setOf("POST")))
            .sections.first { it.source == TimelineSource.Network }.groups.first { it.facet == TimelineFacet.NetworkMethod }
        assertTrue(hiddenPost.asTags)
        assertEquals(false, hiddenPost.values.first { it.method == NetworkMethodUi.Http.POST }.selected)
        assertEquals(true, hiddenPost.values.first { it.method == NetworkMethodUi.Http.GET }.selected)
        assertTrue(network.groups.first { it.facet == TimelineFacet.NetworkStatus }.asTags)

        val families = network.groups.first { it.facet == TimelineFacet.NetworkStatus }.values.map { it.httpFamily }
        assertEquals(listOf(1, 2, 3, 4, 5), families)

        // the other facets are plain values
        val database = summary.toFilterPanel(all, emptyMap()).sections.first { it.source == TimelineSource.Database }
        val statements = database.groups.first { it.facet == TimelineFacet.DatabaseStatement }
        assertTrue(statements.values.all { it.method == null && it.httpFamily == null })
        assertEquals(false, statements.asTags)
    }

    @Test
    fun `the values brought by the events come after the known ones, sorted`() {
        val withDatabases = summary.copy(
            facetCounts = summary.facetCounts + (TimelineFacet.DatabaseName to mapOf("b.db" to 1, "a.db" to 3)),
            sourceCounts = summary.sourceCounts + (TimelineSource.Database to 4),
        )

        val database = withDatabases.toFilterPanel(all, emptyMap()).sections.first { it.source == TimelineSource.Database }

        assertEquals(listOf("a.db", "b.db"), database.groups.first { it.facet == TimelineFacet.DatabaseName }.values.map { it.value })
    }

    @Test
    fun `a facet without known value is not listed before an event brings one`() {
        val database = summary.toFilterPanel(all, emptyMap()).sections.first { it.source == TimelineSource.Database }

        assertTrue(database.groups.none { it.facet == TimelineFacet.DatabaseName })
        // the statements are always known
        assertEquals(
            listOf(TimelineFacet.DatabaseStatement),
            database.groups.map { it.facet },
        )
    }

    @Test
    fun `every value is checked unless it was unchecked, even without event`() {
        val panel = summary.toFilterPanel(all, mapOf(TimelineFacet.NetworkStatus to setOf("5xx")))

        val status = panel.sections.first().groups.first { it.facet == TimelineFacet.NetworkStatus }.values
        assertEquals(TimelineFacet.NetworkStatus.knownValues, status.map { it.value })
        assertEquals(listOf(true, true, true, true, false), status.map { it.selected })
    }

    @Test
    fun `the current session only is an active filter`() {
        val panel = summary.toFilterPanel(all, emptyMap(), currentSessionOnly = true)

        assertTrue(panel.currentSessionOnly)
        assertTrue(panel.hasActiveFilters)
        assertEquals(1, panel.activeFilterCount)
    }

    @Test
    fun `the active filters are the facets selected and the sources hidden`() {
        val none = summary.toFilterPanel(all, emptyMap())
        assertFalse(none.hasActiveFilters)

        val some = summary.toFilterPanel(
            selectedSources = all - TimelineSource.Crash,
            facets = mapOf(TimelineFacet.NetworkMethod to setOf("GET", "POST")),
        )
        assertTrue(some.hasActiveFilters)
        assertEquals(3, some.activeFilterCount)
        assertEquals(2, some.sections.first { it.source == TimelineSource.Network }.activeFacetCount)
        assertFalse(some.sections.first { it.source == TimelineSource.Crash }.enabled)
    }

    @Test
    fun `the network card counts the calls displayed over the calls received`() {
        val card = summary.toFilterPanel(all, emptyMap(), matchingNetworkCount = 2).network

        assertEquals(2, card.matchingCount)
        assertEquals(3, card.totalCount)
        assertFalse(card.isFiltered)
        // every call is displayed when the filters are not counted yet
        assertEquals(3, summary.toFilterPanel(all, emptyMap()).network.matchingCount)
    }

    @Test
    fun `a rule, a hidden value or a duration range is a filter to reset`() {
        val rule = TimelineNetworkRules(statusRules = listOf(TimelineRuleDomainModel("404", exclude = false)))
        assertTrue(summary.toFilterPanel(all, emptyMap(), network = rule).network.isFiltered)
        assertTrue(summary.toFilterPanel(all, mapOf(TimelineFacet.NetworkContent to setOf("IMAGE"))).network.isFiltered)
        // a facet of another source is not one of the network
        assertFalse(summary.toFilterPanel(all, mapOf(TimelineFacet.DatabaseName to setOf("a.db"))).network.isFiltered)
        // the rules count in the badge of the filter button
        assertEquals(1, summary.toFilterPanel(all, emptyMap(), network = rule).activeFilterCount)
    }

    @Test
    fun `the duration range is told by positions of the buckets`() {
        val range = TimelineNetworkRules(duration = TimelineDurationRangeDomainModel(minMs = 100, maxMs = 1_000))
        val card = summary.toFilterPanel(all, emptyMap(), network = range).network

        assertEquals(2, card.durationStart)
        assertEquals(7, card.durationEnd)
        assertEquals("100 ms - 1 s", card.durationLabel)

        val open = TimelineNetworkRules(duration = TimelineDurationRangeDomainModel(minMs = 1_000, maxMs = null))
        val openCard = summary.toFilterPanel(all, emptyMap(), network = open).network
        assertEquals(TIMELINE_DURATION_BUCKETS_MS.size, openCard.durationEnd)
        assertEquals("1 s - ∞", openCard.durationLabel)

        val none = summary.toFilterPanel(all, emptyMap()).network
        assertEquals(0, none.durationStart)
        assertEquals(none.histogram.size, none.durationEnd)
        assertEquals(null, none.durationLabel)
    }

    @Test
    fun `the histogram and the pending calls come from the counts`() {
        val counted = summary.copy(
            facetCounts = summary.facetCounts + mapOf(
                TimelineFacet.NetworkSpeed to mapOf("0" to 4, "100" to 2),
                TimelineFacet.NetworkState to mapOf("PENDING" to 5),
            ),
        )
        val card = counted.toFilterPanel(all, emptyMap()).network

        assertEquals(4, card.histogram[0])
        assertEquals(2, card.histogram[TIMELINE_DURATION_BUCKETS_MS.indexOf(100L)])
        assertEquals(5, card.pendingCount)
    }

    @Test
    fun `the hosts offered are the ones matching the rules`() {
        val hosts = summary.copy(
            facetCounts = summary.facetCounts + (TimelineFacet.NetworkHost to mapOf("ws.postman-echo.com" to 8, "ads.acme.io" to 1, "analytics.postman.io" to 2)),
        )
        val rules = TimelineNetworkRules(
            hostRules = listOf(
                TimelineRuleDomainModel("postman", exclude = false),
                TimelineRuleDomainModel("analytics", exclude = true),
            ),
        )

        val card = hosts.toFilterPanel(all, emptyMap(), network = rules).network

        assertEquals(listOf("ws.postman-echo.com"), card.hosts.map { it.value })
        assertEquals(1, card.hostCount)
        // all the hosts are offered without rule
        assertEquals(3, hosts.toFilterPanel(all, emptyMap()).network.hostCount)
    }
}
