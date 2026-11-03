package io.github.openflocon.flocondesktop.features.timeline.mapper

import io.github.openflocon.domain.timeline.models.TIMELINE_DURATION_BUCKETS_MS
import io.github.openflocon.domain.timeline.models.TimelineDurationRangeDomainModel
import io.github.openflocon.domain.timeline.models.TimelineFacet
import io.github.openflocon.domain.timeline.models.TimelineRuleDomainModel
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkMethodUi
import io.github.openflocon.flocondesktop.features.timeline.TimelineDatabaseRules
import io.github.openflocon.flocondesktop.features.timeline.TimelineNetworkRules
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineAccessibilityFilterUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineAnalyticsFilterUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineBasicFilterUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineCrashFilterUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineDatabaseFilterUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineFilterPanelUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineNetworkFilterUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineSummaryUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineTableFilterUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineWebViewFilterUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.facetGroups
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
    fun `the sources are listed in their order, whatever their events`() {
        val panel = summary.toFilterPanel(all, emptyMap())

        // the adb commands have events, the databases have none: the order does not move
        assertEquals(TimelineSource.entries, panel.sections.map { it.source })
    }

    @Test
    fun `the panel lists every source even without any event or setting`() {
        val panel = TimelineSummaryUiModel.Empty.toFilterPanel(selectedSources = emptySet(), facets = emptyMap())

        assertEquals(TimelineSource.entries, panel.sections.map { it.source })
        // the network card has its own master switch, it does not follow the sources selected
        assertTrue(panel.sections.filterNot { it.source == TimelineSource.Network }.none { it.enabled })
    }

    @Test
    fun `every known value of a facet is listed, with its count`() {
        val methods = summary.toFilterPanel(all, emptyMap()).network.methods

        assertEquals(TimelineFacet.NetworkMethod.knownValues, methods.map { it.value })
        assertEquals(listOf("GET" to 2, "POST" to 1, "PUT" to 0), methods.take(3).map { it.value to it.count })
    }

    @Test
    fun `the methods and the status classes are displayed like in the network screen`() {
        val network = summary.toFilterPanel(all, emptyMap()).network

        val methods = network.methods.associate { it.value to it.method }
        assertEquals(NetworkMethodUi.Http.GET, methods["GET"])
        assertEquals(NetworkMethodUi.WebSocket, methods["WS"])
        assertEquals(NetworkMethodUi.Grpc, methods["GRPC"])
        assertEquals("OPTIONS", (methods["OPTIONS"] as NetworkMethodUi.OTHER).text)

        val hiddenPost = summary.toFilterPanel(all, mapOf(TimelineFacet.NetworkMethod to setOf("POST"))).network.methods
        assertEquals(false, hiddenPost.first { it.method == NetworkMethodUi.Http.POST }.selected)
        assertEquals(true, hiddenPost.first { it.method == NetworkMethodUi.Http.GET }.selected)

        assertEquals(listOf(1, 2, 3, 4, 5), network.statusClasses.map { it.httpFamily })
    }

    @Test
    fun `each source has its own filter model`() {
        val sections = summary.toFilterPanel(all, emptyMap()).sections.associateBy { it.source }

        assertTrue(sections.getValue(TimelineSource.Network) is TimelineNetworkFilterUiModel)
        assertTrue(sections.getValue(TimelineSource.Database) is TimelineDatabaseFilterUiModel)
        assertTrue(sections.getValue(TimelineSource.Analytics) is TimelineAnalyticsFilterUiModel)
        assertTrue(sections.getValue(TimelineSource.Crash) is TimelineCrashFilterUiModel)
        assertTrue(sections.getValue(TimelineSource.Accessibility) is TimelineAccessibilityFilterUiModel)
        // the sources without facet only have their switch
        assertTrue(sections.getValue(TimelineSource.Adb) is TimelineBasicFilterUiModel)
        assertEquals(all, sections.keys)
    }

    @Test
    fun `the database filter has the names and the statements`() {
        val database = summary.toFilterPanel(all, emptyMap()).database

        // the statements are plain values, always known
        assertEquals(TimelineFacet.DatabaseStatement.knownValues, database.statements?.values?.map { it.value })
        assertTrue(database.statements?.values.orEmpty().all { it.method == null && it.httpFamily == null })
        assertEquals(false, database.statements?.asTags)
    }

    @Test
    fun `the values brought by the events come after the known ones, sorted`() {
        val withDatabases = summary.copy(
            facetCounts = summary.facetCounts + (TimelineFacet.DatabaseName to mapOf("b.db" to 1, "a.db" to 3)),
            sourceCounts = summary.sourceCounts + (TimelineSource.Database to 4),
        )

        val database = withDatabases.toFilterPanel(all, emptyMap()).database

        assertEquals(listOf("a.db", "b.db"), database.names.values.map { it.value })
    }

    @Test
    fun `a facet without known value is not listed before an event brings one`() {
        val database = summary.toFilterPanel(all, emptyMap()).database

        // the selector of the databases is always there, to tell that none was seen yet
        assertTrue(database.names.values.isEmpty())
        assertEquals(listOf(TimelineFacet.DatabaseName, TimelineFacet.DatabaseStatement), database.facetGroups().map { it.facet })
    }

    @Test
    fun `a hidden value counts in the facets of its source only`() {
        val panel = summary.toFilterPanel(all, mapOf(TimelineFacet.DatabaseStatement to setOf("SELECT", "INSERT")))

        assertEquals(2, panel.database.activeFacetCount)
        assertEquals(false, panel.database.statements?.values?.first { it.value == "SELECT" }?.selected)
        assertEquals(0, panel.sections.first { it.source == TimelineSource.Crash }.activeFacetCount)
    }

    @Test
    fun `the database rules and the transactions are active filters of the database`() {
        val database = TimelineDatabaseRules(
            rules = listOf(TimelineRuleDomainModel("users", exclude = false), TimelineRuleDomainModel("pragma", exclude = true)),
            hideTransactions = true,
        )
        val panel = summary.toFilterPanel(all, emptyMap(), database = database)

        assertEquals(listOf("users", "pragma"), panel.database.rules.map { it.value })
        assertEquals(listOf(false, true), panel.database.rules.map { it.exclude })
        assertTrue(panel.database.hideTransactions)
        assertEquals(3, panel.database.activeFacetCount)
        assertEquals(3, panel.activeFilterCount)
    }

    @Test
    fun `the tables and the kinds of webview event are facets of their source`() {
        val counted = summary.copy(
            facetCounts = summary.facetCounts + mapOf(
                TimelineFacet.TableName to mapOf("users" to 2),
                TimelineFacet.WebViewKind to mapOf("Console" to 5),
            ),
        )
        val panel = counted.toFilterPanel(all, mapOf(TimelineFacet.TableName to setOf("users")))

        val table = panel.sections.filterIsInstance<TimelineTableFilterUiModel>().single()
        assertEquals(listOf("users"), table.tables.values.map { it.value })
        assertEquals(false, table.tables.values.single().selected)
        assertEquals(1, table.activeFacetCount)

        val kinds = panel.sections.filterIsInstance<TimelineWebViewFilterUiModel>().single().kinds
        assertEquals(TimelineFacet.WebViewKind.knownValues, kinds?.values?.map { it.value })
        assertEquals(5, kinds?.values?.first { it.value == "Console" }?.count)
        assertTrue(kinds?.asTags == true)
    }

    @Test
    fun `every value is checked unless it was unchecked, even without event`() {
        val panel = summary.toFilterPanel(all, mapOf(TimelineFacet.NetworkStatus to setOf("5xx")))

        val status = panel.network.statusClasses
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
    fun `the path rules of the network are shown and are filters to reset`() {
        val rules = TimelineNetworkRules(pathRules = listOf(TimelineRuleDomainModel("/raw", exclude = true)))

        val panel = summary.toFilterPanel(all, emptyMap(), network = rules)

        assertEquals(listOf("/raw"), panel.network.pathRules.map { it.value })
        assertTrue(panel.network.isFiltered)
        assertEquals(1, panel.activeFilterCount)
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

    private val TimelineFilterPanelUiModel.network
        get() = sections.filterIsInstance<TimelineNetworkFilterUiModel>().single()

    private val TimelineFilterPanelUiModel.database
        get() = sections.filterIsInstance<TimelineDatabaseFilterUiModel>().single()
}
