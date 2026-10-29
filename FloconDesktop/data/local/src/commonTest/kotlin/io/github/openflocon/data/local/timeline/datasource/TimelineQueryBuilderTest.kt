package io.github.openflocon.data.local.timeline.datasource

import io.github.openflocon.domain.timeline.models.TimelineFacet
import io.github.openflocon.domain.timeline.models.TimelineFilterDomainModel
import io.github.openflocon.domain.timeline.models.TimelineSource
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TimelineQueryBuilderTest {

    private fun build(filter: TimelineFilterDomainModel = TimelineFilterDomainModel()) = buildTimelineQuery(
        deviceId = "device",
        packageName = "com.app",
        filter = filter,
    )

    // the sessions are looked up in the tables that store them
    private val sessionParams = listOf<Any>("device", "com.app", "device", "com.app", "device", "com.app")

    // every source but the app starts, which are sessions and not events
    private val eventSources = TimelineSource.entries.size - 1

    // a network call is two events, its request and its response
    private val eventBranches = eventSources + 1

    @Test
    fun `every source is merged by a union sorted from the newest`() {
        val query = build()

        // the events of every source and the app starts
        assertEquals(eventBranches, Regex("UNION ALL").findAll(query.sql).count())
        assertTrue(query.sql.contains("FROM FloconNetworkCallEntity"))
        assertTrue(query.sql.contains("FROM DatabaseQueryLogEntity"))
        assertTrue(query.sql.contains("FROM AnalyticsItemEntity"))
        assertTrue(query.sql.contains("FROM AccessibilityEventEntity"))
        assertTrue(query.sql.contains("FROM CrashReportEntity"))
        assertTrue(query.sql.contains("FROM TableItemEntity JOIN TableEntity"))
        assertTrue(query.sql.contains("FROM DeeplinkEntity"))
        assertTrue(query.sql.contains("FROM AdbCommandHistoryEntity"))
        assertTrue(
            query.sql.endsWith(
                "ORDER BY u.ts DESC, (u.kind = '$KIND_APP_START') ASC, (u.kind = '$KIND_NETWORK_RESPONSE') DESC, u.id DESC"
            )
        )
    }

    @Test
    fun `the network calls carry their method, status code and duration`() {
        val query = build(TimelineFilterDomainModel(sources = setOf(TimelineSource.Network)))

        assertTrue(query.sql.contains("WHEN 'WEBSOCKET' THEN 'WS'"))
        assertTrue(query.sql.contains("response_http_responseHttpCode"))
        assertTrue(query.sql.contains("response_graphql_responseHttpCode"))
        assertTrue(query.sql.contains("response_durationMs AS durationMs"))
    }

    @Test
    fun `the network calls tell if their response is an image, the other events never are`() {
        val query = build(TimelineFilterDomainModel(sources = setOf(TimelineSource.Network, TimelineSource.Database)))

        assertTrue(query.sql.contains("COALESCE(response_isImage, 0) AS isImage"))
        assertTrue(query.sql.contains("0 AS isImage"))
        assertTrue(query.sql.contains("u.isImage"))
    }

    @Test
    fun `a network call with a response has a second event at the time the response arrived`() {
        val query = build(TimelineFilterDomainModel(sources = setOf(TimelineSource.Network)))

        assertEquals(1, Regex("UNION ALL").findAll(query.sql).count())
        assertTrue(query.sql.contains("'$KIND_NETWORK_RESPONSE' AS kind, callId AS id"))
        assertTrue(query.sql.contains("request_startTime + CAST(response_durationMs AS INTEGER) AS ts"))
        // a call still waiting for its response has no response event
        assertTrue(query.sql.contains("response_durationMs IS NOT NULL"))
    }

    @Test
    fun `each event carries the start of its session`() {
        val query = build(TimelineFilterDomainModel(sources = setOf(TimelineSource.Crash)))

        assertTrue(query.sql.startsWith("WITH sessions(appInstance) AS ("))
        assertTrue(query.sql.contains("SELECT appInstance FROM FloconNetworkCallEntity"))
        assertTrue(query.sql.contains("SELECT appInstance FROM DatabaseQueryLogEntity"))
        assertTrue(query.sql.contains("SELECT appInstance FROM AnalyticsItemEntity"))
        assertEquals(3, Regex(" UNION SELECT").findAll(query.sql).count() + 1)
        assertTrue(query.sql.contains("(SELECT MAX(appInstance) FROM sessions WHERE appInstance <= u.ts) AS sessionStart"))
        // the sessions come first in the query, then the crashes
        assertEquals(sessionParams + listOf<Any>("device", "com.app"), query.params)
    }

    @Test
    fun `the app starts are rows at their time, even without events`() {
        val query = build(TimelineFilterDomainModel(sources = setOf(TimelineSource.AppStart)))

        assertFalse(query.sql.contains("request_url AS title"))
        assertTrue(query.sql.contains("SELECT '$KIND_APP_START' AS kind, CAST(appInstance AS TEXT) AS id, appInstance AS ts"))
        // the sessions of the CTE, then the ones of the app starts
        assertEquals(sessionParams + sessionParams, query.params)
    }

    @Test
    fun `the app starts are hidden when the app start source is not selected or the text is used`() {
        val withoutSource = build(TimelineFilterDomainModel(sources = setOf(TimelineSource.Crash)))
        val withText = build(TimelineFilterDomainModel(text = "login"))

        assertFalse(withoutSource.sql.contains("'$KIND_APP_START' AS kind"))
        assertFalse(withText.sql.contains("'$KIND_APP_START' AS kind"))
    }

    @Test
    fun `only the opened deeplinks are events, not the ones declared by the app`() {
        val query = build(TimelineFilterDomainModel(sources = setOf(TimelineSource.Deeplink)))

        assertTrue(query.sql.contains("isHistory = 1"))
        assertEquals(sessionParams + listOf<Any>("device", "com.app"), query.params)
    }

    @Test
    fun `only the selected sources are queried`() {
        val query = build(TimelineFilterDomainModel(sources = setOf(TimelineSource.Crash)))

        assertFalse(query.sql.contains("UNION ALL"))
        assertFalse(query.sql.contains("request_url AS title"))
        assertTrue(query.sql.contains("FROM CrashReportEntity"))
    }

    @Test
    fun `the webview events are scoped by the device and the app, and keep no session`() {
        val query = build(TimelineFilterDomainModel(sources = setOf(TimelineSource.WebView), appInstance = 1000L))

        assertTrue(query.sql.contains("FROM WebViewEventEntity"))
        assertTrue(query.sql.contains("'$KIND_WEBVIEW' AS kind"))
        // the events do not store the session: the filter on the session is not applied to them
        assertEquals(sessionParams + listOf<Any>("device", "com.app"), query.params)
    }

    @Test
    fun `the adb commands are only scoped by the device`() {
        val query = build(TimelineFilterDomainModel(sources = setOf(TimelineSource.Adb)))

        assertEquals(sessionParams + listOf<Any>("device"), query.params)
    }

    @Test
    fun `the database logs are limited to the most recent ones`() {
        val query = build(TimelineFilterDomainModel(sources = setOf(TimelineSource.Database)))

        assertTrue(query.sql.contains("ORDER BY timestamp DESC LIMIT $MAX_DATABASE_LOGS"))
    }

    @Test
    fun `the text filter is applied on every source`() {
        val query = build(TimelineFilterDomainModel(text = "login"))

        // every source filters on two columns
        assertEquals(eventBranches * 2, Regex("LIKE \\?").findAll(query.sql).count())
        assertEquals(eventBranches * 2, query.params.count { it == "%login%" })
    }

    @Test
    fun `the session filter is only applied on the sources that store it`() {
        val query = build(TimelineFilterDomainModel(appInstance = 42L))

        // network (request and response), database, analytics, accessibility and the app starts
        assertEquals(6, Regex("appInstance = \\?").findAll(query.sql).count())
        assertEquals(6, query.params.count { it == 42L })
    }

    @Test
    fun `a blank text is ignored`() {
        val query = build(TimelineFilterDomainModel(text = "   "))

        // the text is bound as a parameter, the other LIKE are the statements of the database
        assertFalse(query.sql.contains("LIKE ?"))
    }

    @Test
    fun `without any source nothing is returned`() {
        val query = build(TimelineFilterDomainModel(sources = emptySet()))

        assertTrue(query.sql.endsWith("WHERE 0"))
        assertTrue(query.params.isEmpty())
    }

    @Test
    fun `the summary counts every source whatever the selected ones`() {
        val query = buildTimelineSummaryQuery(
            deviceId = "device",
            packageName = "com.app",
            filter = TimelineFilterDomainModel(sources = setOf(TimelineSource.Crash)),
        )

        assertTrue(query.sql.contains("FROM FloconNetworkCallEntity"))
        assertTrue(query.sql.contains("GROUP BY kind"))
        assertTrue(query.sql.contains("statusCode >= 400"))
        // a network call is counted once, not for its response
        assertFalse(query.sql.contains(KIND_NETWORK_RESPONSE))
        // the sessions are counted as the app starts
        assertTrue(query.sql.contains("SELECT '$KIND_APP_START' AS kind, COUNT(*) AS count"))
    }

    @Test
    fun `the summary follows the text of the filter`() {
        val query = buildTimelineSummaryQuery(
            deviceId = "device",
            packageName = "com.app",
            filter = TimelineFilterDomainModel(text = "login"),
        )

        assertEquals(eventSources * 2, query.params.count { it == "%login%" })
    }

    @Test
    fun `a facet hides the values unchecked, for the request and the response of a call`() {
        val query = build(
            TimelineFilterDomainModel(
                sources = setOf(TimelineSource.Network),
                facets = mapOf(
                    TimelineFacet.NetworkMethod to setOf("GET", "POST"),
                    TimelineFacet.NetworkStatus to setOf("5xx"),
                ),
            )
        )

        // the request and the response events, each filtered
        assertEquals(2, Regex("NOT IN \\(\\?, \\?\\)").findAll(query.sql).count())
        assertEquals(2, Regex("\\|\\| 'xx' NOT IN \\(\\?\\)").findAll(query.sql).count())
        // an event without value, ex: a call without http code, is not hidden
        assertEquals(2, Regex("\\|\\| 'xx' IS NULL OR").findAll(query.sql).count())
        // the sessions, then for the request and the response: device, package, method values, status values
        val branch = listOf<Any>("device", "com.app", "GET", "POST", "5xx")
        assertEquals(sessionParams + branch + branch, query.params)
    }

    @Test
    fun `every member of the union has the facet columns`() {
        val query = build()

        // 1 per member: the events of every source, the response of the network calls and the app starts
        assertEquals(eventBranches + 1, Regex("AS facetA\\b").findAll(query.sql).count())
        assertEquals(eventBranches + 1, Regex("AS facetB\\b").findAll(query.sql).count())
    }

    @Test
    fun `a database statement is told from the first word of its query`() {
        val query = build(TimelineFilterDomainModel(sources = setOf(TimelineSource.Database)))

        assertTrue(query.sql.contains("WHEN UPPER(LTRIM(sqlQuery)) LIKE 'SELECT%' OR UPPER(LTRIM(sqlQuery)) LIKE 'WITH%' THEN 'SELECT'"))
        assertTrue(query.sql.contains("LIKE 'BEGIN%'"))
        assertTrue(query.sql.contains("THEN 'TRANSACTION'"))
        assertTrue(query.sql.contains("ELSE 'OTHER' END AS facetDatabaseStatement"))
    }

    @Test
    fun `every member of the union has the same facet columns`() {
        val query = build()

        // 1 per member: the events of every source, the response of the network calls and the app starts
        TimelineFacet.entries.forEach {
            assertEquals(eventBranches + 1, Regex("AS facet${it.name}\\b").findAll(query.sql).count(), it.name)
        }
    }

    @Test
    fun `the network calls can be told by state, duration, content and host`() {
        val query = build(
            TimelineFilterDomainModel(
                sources = setOf(TimelineSource.Network),
                facets = mapOf(
                    TimelineFacet.NetworkState to setOf("PENDING"),
                    TimelineFacet.NetworkSpeed to setOf(">1s"),
                    TimelineFacet.NetworkContent to setOf("IMAGE"),
                    TimelineFacet.NetworkHost to setOf("ads.acme.io"),
                ),
            )
        )

        assertTrue(query.sql.contains("WHEN response_durationMs IS NULL THEN 'PENDING'"))
        assertTrue(query.sql.contains("WHEN response_durationMs < 100 THEN '<100ms'"))
        assertTrue(query.sql.contains("THEN 'IMAGE' ELSE 'OTHER' END"))
        assertTrue(query.sql.contains("request_domainFormatted NOT IN (?)"))
        // a call still waiting for its response has no duration: it is not hidden by the duration
        assertTrue(query.sql.contains("IS NULL OR CASE WHEN response_durationMs IS NULL THEN NULL"))
        val branch = listOf<Any>("device", "com.app", "PENDING", ">1s", "IMAGE", "ads.acme.io")
        assertEquals(sessionParams + branch + branch, query.params)
    }

    @Test
    fun `the statements unchecked are hidden`() {
        val query = build(
            TimelineFilterDomainModel(
                sources = setOf(TimelineSource.Database),
                facets = mapOf(TimelineFacet.DatabaseStatement to setOf("TRANSACTION", "PRAGMA")),
            )
        )

        assertTrue(query.sql.contains(" NOT IN (?, ?)"))
        assertEquals(sessionParams + listOf("device", "com.app", "TRANSACTION", "PRAGMA"), query.params)
    }

    @Test
    fun `nothing is hidden by default`() {
        val query = build(TimelineFilterDomainModel(sources = setOf(TimelineSource.Network, TimelineSource.Database)))

        assertFalse(query.sql.contains("NOT IN"))
    }

    @Test
    fun `the crashes can be hidden by exception`() {
        val query = build(
            TimelineFilterDomainModel(
                sources = setOf(TimelineSource.Crash),
                facets = mapOf(TimelineFacet.CrashException to setOf("java.lang.OutOfMemoryError")),
            )
        )

        assertTrue(query.sql.contains("exceptionType AS facetCrashException"))
        assertTrue(query.sql.contains("exceptionType NOT IN (?)"))
        assertEquals(sessionParams + listOf("device", "com.app", "java.lang.OutOfMemoryError"), query.params)
    }

    @Test
    fun `the facets of a source that is not selected are ignored`() {
        val query = build(
            TimelineFilterDomainModel(
                sources = setOf(TimelineSource.Crash),
                facets = mapOf(TimelineFacet.NetworkMethod to setOf("GET")),
            )
        )

        assertFalse(query.params.contains("GET"))
        assertFalse(query.sql.contains("request_url AS title"))
    }

    @Test
    fun `the values of each facet are counted without the selection of the facet itself`() {
        val query = buildTimelineFacetCountsQuery(
            deviceId = "device",
            packageName = "com.app",
            filter = TimelineFilterDomainModel(
                facets = mapOf(
                    TimelineFacet.NetworkMethod to setOf("GET"),
                    TimelineFacet.NetworkStatus to setOf("2xx"),
                ),
            ),
        )

        TimelineFacet.entries.forEach {
            assertTrue(query.sql.contains("SELECT '${it.name}' AS facet"))
        }
        // each count ignores its own facet and applies the others of the source: GET is applied to every network count but the methods
        val otherNetworkFacets = TimelineFacet.entries.count { it.source == TimelineSource.Network } - 1
        assertEquals(otherNetworkFacets, query.params.count { it == "GET" })
        assertEquals(otherNetworkFacets, query.params.count { it == "2xx" })
        // a network call is counted once, not for its response
        assertFalse(query.sql.contains(KIND_NETWORK_RESPONSE))
    }

    @Test
    fun `the session counts follow the selected events`() {
        val query = buildTimelineSessionCountsQuery(
            deviceId = "device",
            packageName = "com.app",
            filter = TimelineFilterDomainModel(sources = setOf(TimelineSource.Crash)),
        )

        assertTrue(query.sql.contains("GROUP BY sessionStart"))
        assertTrue(query.sql.contains("kind <> '$KIND_APP_START'"))
        assertTrue(query.sql.contains("FROM CrashReportEntity"))
        assertFalse(query.sql.contains("request_url AS title"))
    }
}
