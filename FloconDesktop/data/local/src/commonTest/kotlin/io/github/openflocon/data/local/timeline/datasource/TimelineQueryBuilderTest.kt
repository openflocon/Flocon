package io.github.openflocon.data.local.timeline.datasource

import io.github.openflocon.data.local.timelinelog.dao.MAX_TIMELINE_LOGS
import io.github.openflocon.domain.timeline.models.TimelineDurationRangeDomainModel
import io.github.openflocon.domain.timeline.models.TimelineEventRef
import io.github.openflocon.domain.timeline.models.TimelineFacet
import io.github.openflocon.domain.timeline.models.TimelineFilterDomainModel
import io.github.openflocon.domain.timeline.models.TimelineRuleDomainModel
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
    private val sessionParams = listOf<Any>("device", "com.app", "device", "com.app", "device", "com.app", "device", "com.app")

    // every source but the app starts, which are sessions and not events
    private val eventSources = TimelineSource.entries.size - 1

    // a network call is two events, its request and its response
    private val eventBranches = eventSources + 1

    // how many columns the text is looked for in, by source
    private val textColumnCounts = mapOf(
        TimelineSource.Network to 2,
        TimelineSource.Database to 2,
        TimelineSource.Analytics to 3, // the properties too
        TimelineSource.Accessibility to 4, // the class and the screen too
        TimelineSource.WebView to 2,
        TimelineSource.Crash to 3, // the stack trace too
        TimelineSource.Table to 3, // the columns too
        TimelineSource.Deeplink to 4, // the description and the parameters too
        TimelineSource.Adb to 2,
        TimelineSource.Log to 2, // the message and the type
    )

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
        assertTrue(query.sql.contains("FROM TimelineLogEntity"))
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
        assertTrue(query.sql.contains("CASE WHEN response_responseError IS NOT NULL THEN 1 ELSE 0 END AS hasError"))
        assertTrue(query.sql.contains("0 AS hasError"))
        assertTrue(query.sql.contains("u.hasError"))
    }

    @Test
    fun `the network calls carry the size of their response, the other events never do`() {
        val query = build()

        // the request and the response events of a call both carry it
        assertEquals(2, Regex("response_responseByteSize AS responseByteSize").findAll(query.sql).count())
        assertTrue(query.sql.contains("NULL AS responseByteSize"))
        assertTrue(query.sql.contains("u.responseByteSize"))
    }

    @Test
    fun `every member of the union carries the transaction columns, only the database queries fill them`() {
        val query = build()

        // the events of every source and the app starts: same columns everywhere, the union is positional
        assertEquals(eventBranches + 1, Regex("AS transactionId").findAll(query.sql).count())
        assertEquals(eventBranches + 1, Regex("AS isTransactionControl").findAll(query.sql).count())
        assertTrue(query.sql.contains("transactionId AS transactionId"))
        assertTrue(query.sql.contains("u.transactionId, u.txQueryCount, u.txStart, u.txEnd, u.isTransactionControl"))
        assertTrue(query.sql.contains("isTransaction AS isTransactionControl"))
        // and the nested transaction of the query, with the transactions that hold it
        assertEquals(eventBranches + 1, Regex("AS nestedTransactionId").findAll(query.sql).count())
        assertEquals(eventBranches + 1, Regex("AS nestedPath").findAll(query.sql).count())
        assertTrue(query.sql.contains("u.nestedTransactionId, u.nestedQueryCount, u.transactionDepth, u.nestedPath"))
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
        assertTrue(query.sql.contains("SELECT appInstance FROM TimelineLogEntity"))
        assertEquals(4, Regex(" UNION SELECT").findAll(query.sql).count() + 1)
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

        // every source filters on its own columns, the response of a network call too
        val expected = textColumnCounts.values.sum() + textColumnCounts.getValue(TimelineSource.Network)
        assertEquals(expected, Regex("LIKE \\?").findAll(query.sql).count())
        assertEquals(expected, query.params.count { it == "%login%" })
    }

    @Test
    fun `the session filter is only applied on the sources that store it`() {
        val query = build(TimelineFilterDomainModel(appInstance = 42L))

        // network (request and response), database, analytics, accessibility, logs and the app starts
        assertEquals(7, Regex("appInstance = \\?").findAll(query.sql).count())
        assertEquals(7, query.params.count { it == 42L })
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

        assertEquals(textColumnCounts.values.sum(), query.params.count { it == "%login%" })
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
    fun `a database statement is told from the first word of its query`() {
        val query = build(TimelineFilterDomainModel(sources = setOf(TimelineSource.Database)))

        assertTrue(query.sql.contains("WHEN UPPER(LTRIM(sqlQuery)) LIKE 'SELECT%' OR UPPER(LTRIM(sqlQuery)) LIKE 'WITH%' THEN 'SELECT'"))
        assertTrue(query.sql.contains("LIKE 'BEGIN%'"))
        assertTrue(query.sql.contains("THEN 'TRANSACTION'"))
        assertTrue(query.sql.contains("ELSE 'OTHER' END AS facetDatabaseStatement"))
    }

    @Test
    fun `what Room runs for itself is told apart, with the whole of its transaction`() {
        val query = build(TimelineFilterDomainModel(sources = setOf(TimelineSource.Database)))

        // first of the statements: a query on the modification log of Room, or any query of a transaction that has one
        assertTrue(query.sql.contains("CASE WHEN (sqlQuery LIKE '%room_table_modification_log%' OR (transactionId IS NOT NULL AND EXISTS ("))
        assertTrue(query.sql.contains("THEN 'ROOM'"))
        assertTrue(query.sql.indexOf("THEN 'ROOM'") < query.sql.indexOf("THEN 'SELECT'"))
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
                    TimelineFacet.NetworkSpeed to setOf("1000"),
                    TimelineFacet.NetworkContent to setOf("IMAGE"),
                    TimelineFacet.NetworkHost to setOf("ads.acme.io"),
                ),
            )
        )

        assertTrue(query.sql.contains("WHEN response_durationMs IS NULL THEN 'PENDING'"))
        assertTrue(query.sql.contains("WHEN response_durationMs >= 1000 THEN '1000'"))
        assertTrue(query.sql.contains("THEN 'WEBSOCKET' WHEN COALESCE(response_isImage, 0) = 1 THEN 'IMAGE' ELSE 'OTHER' END"))
        assertTrue(query.sql.contains("request_domainFormatted NOT IN (?)"))
        // a call still waiting for its response has no duration: it is not hidden by the duration
        assertTrue(query.sql.contains("IS NULL OR CASE WHEN response_durationMs IS NULL THEN NULL"))
        val branch = listOf<Any>("device", "com.app", "PENDING", "1000", "IMAGE", "ads.acme.io")
        assertEquals(sessionParams + branch + branch, query.params)
    }

    @Test
    fun `the status rules keep the included codes and hide the excluded ones`() {
        val query = build(
            TimelineFilterDomainModel(
                sources = setOf(TimelineSource.Network),
                networkStatusRules = listOf(
                    TimelineRuleDomainModel("101", exclude = false),
                    TimelineRuleDomainModel("500-503", exclude = true),
                ),
            )
        )

        assertTrue(query.sql.contains("AND (CAST(COALESCE(response_http_responseHttpCode, response_graphql_responseHttpCode) AS INTEGER) BETWEEN ? AND ?)"))
        assertTrue(query.sql.contains("IS NULL OR NOT (CAST(COALESCE(response_http_responseHttpCode, response_graphql_responseHttpCode) AS INTEGER) BETWEEN ? AND ?)"))
        val branch = listOf<Any>("device", "com.app", 101, 101, 500, 503)
        assertEquals(sessionParams + branch + branch, query.params)
    }

    @Test
    fun `the host rules are words of the host`() {
        val query = build(
            TimelineFilterDomainModel(
                sources = setOf(TimelineSource.Network),
                networkHostRules = listOf(
                    TimelineRuleDomainModel("postman", exclude = false),
                    TimelineRuleDomainModel("analytics", exclude = true),
                ),
            )
        )

        assertTrue(query.sql.contains("AND (request_domainFormatted LIKE ?)"))
        assertTrue(query.sql.contains("AND COALESCE(request_domainFormatted, '') NOT LIKE ?"))
        val branch = listOf<Any>("device", "com.app", "%postman%", "%analytics%")
        assertEquals(sessionParams + branch + branch, query.params)
    }

    @Test
    fun `a duration range hides the calls without duration`() {
        val query = build(
            TimelineFilterDomainModel(
                sources = setOf(TimelineSource.Network),
                networkDuration = TimelineDurationRangeDomainModel(minMs = 100, maxMs = 1_000),
            )
        )

        assertTrue(query.sql.contains("AND CAST(response_durationMs AS INTEGER) >= ? AND CAST(response_durationMs AS INTEGER) <= ?"))
        val branch = listOf<Any>("device", "com.app", 100L, 1_000L)
        assertEquals(sessionParams + branch + branch, query.params)
    }

    @Test
    fun `a duration range without maximum has no upper bound`() {
        val query = build(
            TimelineFilterDomainModel(
                sources = setOf(TimelineSource.Network),
                networkDuration = TimelineDurationRangeDomainModel(minMs = 1_000, maxMs = null),
            )
        )

        assertFalse(query.sql.contains("<= ?"))
    }

    @Test
    fun `the path rules are words of the path of the network calls only`() {
        val rules = listOf(
            TimelineRuleDomainModel("/raw", exclude = true),
            TimelineRuleDomainModel("/users", exclude = false),
        )

        val network = build(TimelineFilterDomainModel(sources = setOf(TimelineSource.Network), networkPathRules = rules))
        assertTrue(network.sql.contains("AND (request_queryFormatted LIKE ?)"))
        assertTrue(network.sql.contains("AND COALESCE(request_queryFormatted, '') NOT LIKE ?"))
        val branch = listOf<Any>("device", "com.app", "%/users%", "%/raw%")
        assertEquals(sessionParams + branch + branch, network.params)

        val other = build(TimelineFilterDomainModel(sources = setOf(TimelineSource.Database), networkPathRules = rules))
        assertFalse(other.sql.contains("request_queryFormatted LIKE"))

        val disabled = build(TimelineFilterDomainModel(sources = setOf(TimelineSource.Network), networkEnabled = false, networkPathRules = rules))
        assertFalse(disabled.sql.contains("request_queryFormatted LIKE"))
    }

    @Test
    fun `the network filters are ignored when they are disabled`() {
        val query = build(
            TimelineFilterDomainModel(
                sources = setOf(TimelineSource.Network),
                networkEnabled = false,
                facets = mapOf(TimelineFacet.NetworkMethod to setOf("GET")),
                networkStatusRules = listOf(TimelineRuleDomainModel("404", exclude = false)),
                networkHostRules = listOf(TimelineRuleDomainModel("acme", exclude = true)),
                networkDuration = TimelineDurationRangeDomainModel(minMs = 100, maxMs = null),
            )
        )

        assertFalse(query.params.contains("GET"))
        assertFalse(query.sql.contains("BETWEEN"))
        assertFalse(query.sql.contains("NOT LIKE"))
        assertFalse(query.sql.contains(">= ?"))
    }

    @Test
    fun `the durations are counted without the duration range`() {
        val query = buildTimelineFacetCountsQuery(
            deviceId = "device",
            packageName = "com.app",
            filter = TimelineFilterDomainModel(
                networkDuration = TimelineDurationRangeDomainModel(minMs = 100, maxMs = 1_000),
            ),
        )

        // only the other network facets (the counts of status, host...) apply the range
        val otherNetworkFacets = TimelineFacet.entries.count { it.source == TimelineSource.Network } - 1
        assertEquals(otherNetworkFacets, query.params.count { it == 100L })
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
    fun `each event carries its value for every facet, to be excluded by them`() {
        val sql = build().sql

        assertTrue(sql.contains("json_object('NetworkMethod', u.facetNetworkMethod"))
        TimelineFacet.entries.forEach {
            assertTrue(sql.contains("'${it.name}', u.facet${it.name}"), it.name)
        }
        assertTrue(sql.contains(") AS facetValues"))
    }

    @Test
    fun `the tables and the kinds of webview event can be hidden`() {
        val table = build(
            TimelineFilterDomainModel(
                sources = setOf(TimelineSource.Table),
                facets = mapOf(TimelineFacet.TableName to setOf("users")),
            )
        )
        assertTrue(table.sql.contains("TableEntity.name AS facetTableName"))
        assertTrue(table.sql.contains("TableEntity.name NOT IN (?)"))
        assertTrue(table.params.contains("users"))

        val webView = build(
            TimelineFilterDomainModel(
                sources = setOf(TimelineSource.WebView),
                facets = mapOf(TimelineFacet.WebViewKind to setOf("Console")),
            )
        )
        assertTrue(webView.sql.contains("kind AS facetWebViewKind"))
        assertTrue(webView.sql.contains("kind NOT IN (?)"))
        assertTrue(webView.params.contains("Console"))
    }

    @Test
    fun `the database rules are words of the sql query`() {
        val query = build(
            TimelineFilterDomainModel(
                sources = setOf(TimelineSource.Database),
                databaseRules = listOf(
                    TimelineRuleDomainModel("users", exclude = false),
                    TimelineRuleDomainModel("sqlite_master", exclude = true),
                ),
            )
        )

        assertTrue(query.sql.contains("AND (sqlQuery LIKE ?)"))
        assertTrue(query.sql.contains("AND COALESCE(sqlQuery, '') NOT LIKE ?"))
        assertEquals(sessionParams + listOf("device", "com.app", "%users%", "%sqlite_master%"), query.params)
    }

    @Test
    fun `the transactions of the database are hidden on demand`() {
        val sources = setOf(TimelineSource.Database)

        assertTrue(build(TimelineFilterDomainModel(sources = sources, hideDatabaseTransactions = true)).sql.contains("AND isTransaction = 0"))
        assertFalse(build(TimelineFilterDomainModel(sources = sources)).sql.contains("AND isTransaction = 0"))
        // the rules of the database are not applied to the other sources
        assertFalse(
            build(
                TimelineFilterDomainModel(
                    sources = setOf(TimelineSource.Analytics),
                    databaseRules = listOf(TimelineRuleDomainModel("users", exclude = false)),
                    hideDatabaseTransactions = true,
                )
            ).sql.contains("sqlQuery LIKE"),
        )
    }

    @Test
    fun `the text also looks in the details of the events`() {
        val query = build(TimelineFilterDomainModel(text = "abc"))

        listOf("propertiesValues", "stackTrace", "className", "screen", "parametersAsJson", "TableItemEntity.columnsNames").forEach {
            assertTrue(query.sql.contains("$it LIKE ?"), it)
        }
    }

    @Test
    fun `the current session only keeps the events since the latest app start`() {
        val query = build(TimelineFilterDomainModel(sources = setOf(TimelineSource.Crash, TimelineSource.AppStart), currentSessionOnly = true))

        // the crashes do not store their session: they are kept by time
        assertTrue(query.sql.contains("timestamp >= COALESCE((SELECT MAX(appInstance) FROM ("))
        // the app starts are reduced to the latest one
        assertTrue(query.sql.contains("appInstance = COALESCE((SELECT MAX(appInstance) FROM ("))
        // the sessions of the query, the crash branch and the latest session it is compared to,
        // then the app starts and the latest session
        assertEquals(sessionParams + listOf("device", "com.app") + sessionParams + sessionParams + sessionParams, query.params)
    }

    @Test
    fun `every session is kept by default`() {
        val query = build(TimelineFilterDomainModel(sources = setOf(TimelineSource.Crash)))

        assertFalse(query.sql.contains("COALESCE((SELECT MAX(appInstance)"))
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

    private fun logs(filter: TimelineFilterDomainModel = TimelineFilterDomainModel(sources = setOf(TimelineSource.Log))) = build(filter)

    @Test
    fun `logBranchPresentWhenSourceSelected`() {
        assertTrue(logs().sql.contains("FROM TimelineLogEntity"))
        assertTrue(logs().sql.contains("'$KIND_LOG' AS kind, CAST(id AS TEXT) AS id"))
        assertFalse(build(TimelineFilterDomainModel(sources = setOf(TimelineSource.Crash))).sql.contains("value AS title"))
        assertTrue(logs().sql.contains("ORDER BY timestamp DESC LIMIT $MAX_TIMELINE_LOGS"))
    }

    @Test
    fun `logRowsScopedToDeviceAndPackage`() {
        val query = logs(TimelineFilterDomainModel(sources = setOf(TimelineSource.Log), appInstance = 1000L))

        assertTrue(query.sql.contains("WHERE deviceId = ? AND packageName = ? AND appInstance = ?"))
        assertEquals(sessionParams + listOf<Any>("device", "com.app", 1000L), query.params)
    }

    @Test
    fun `logRowsUseTitleValueSubtitleType`() {
        val sql = logs().sql

        assertTrue(sql.contains("value AS title, type AS subtitle"))
        assertTrue(sql.contains("u.facetLogLevel AS level"))
        assertTrue(sql.contains("type AS facetLogType"))
        assertTrue(sql.contains("level AS facetLogLevel"))
    }

    @Test
    fun `levelFacetFiltersRows`() {
        val query = logs(
            TimelineFilterDomainModel(
                sources = setOf(TimelineSource.Log),
                facets = mapOf(TimelineFacet.LogLevel to setOf("ERROR")),
            ),
        )

        assertTrue(query.sql.contains("(level IS NULL OR level NOT IN (?))"))
        assertTrue(query.params.contains("ERROR"))
        assertEquals(listOf("DEBUG", "INFO", "WARNING", "ERROR"), TimelineFacet.LogLevel.knownValues)
    }

    @Test
    fun `typeFacetFiltersRows`() {
        val query = logs(
            TimelineFilterDomainModel(
                sources = setOf(TimelineSource.Log),
                facets = mapOf(TimelineFacet.LogType to setOf("userAction")),
            ),
        )

        assertTrue(query.sql.contains("(type IS NULL OR type NOT IN (?))"))
        assertTrue(query.params.contains("userAction"))
    }

    private val reserved = "LOWER(type) IN ('navigation', 'useraction', 'lifecycle')"

    private fun taggedLogs(filter: TimelineFilterDomainModel) = logs(filter.copy(sources = setOf(TimelineSource.Log)))

    @Test
    fun `the tagged level facet reads the level of the tagged logs only`() {
        val sql = logs().sql
        assertTrue(sql.contains("CASE WHEN $reserved THEN NULL ELSE level END AS facetLogTaggedLevel"))

        val query = taggedLogs(TimelineFilterDomainModel(facets = mapOf(TimelineFacet.LogTaggedLevel to setOf("INFO"))))
        val expression = "CASE WHEN $reserved THEN NULL ELSE level END"
        assertTrue(query.sql.contains("($expression IS NULL OR $expression NOT IN (?))"))
        assertTrue(query.params.contains("INFO"))
    }

    @Test
    fun `the facet counts contain the tagged level`() {
        val counts = buildTimelineFacetCountsQuery("device", "com.app", TimelineFilterDomainModel())
        assertTrue(counts.sql.contains("SELECT 'LogTaggedLevel' AS facet, facetLogTaggedLevel AS value"))
    }

    @Test
    fun `tagged logs disabled keeps the reserved types only`() {
        val query = taggedLogs(
            TimelineFilterDomainModel(
                logTaggedEnabled = false,
                logTagRules = listOf(TimelineRuleDomainModel("bill", exclude = false)),
                facets = mapOf(TimelineFacet.LogTaggedLevel to setOf("INFO")),
            ),
        )
        assertTrue(query.sql.contains("AND $reserved"))
        // the rules and the levels have nothing left to narrow
        assertFalse(query.sql.contains("LIKE"))
        assertFalse(query.sql.contains("NOT IN"))
        assertFalse(query.params.contains("%bill%"))
        assertFalse(query.params.contains("INFO"))
    }

    @Test
    fun `tag rules include and exclude with a contains match that spares the reserved types`() {
        val query = taggedLogs(
            TimelineFilterDomainModel(
                logTagRules = listOf(
                    TimelineRuleDomainModel("bill", exclude = false),
                    TimelineRuleDomainModel("pay", exclude = false),
                    TimelineRuleDomainModel("debug", exclude = true),
                ),
            ),
        )
        assertTrue(query.sql.contains("AND ($reserved OR type LIKE ? ESCAPE '\\' OR type LIKE ? ESCAPE '\\')"))
        assertTrue(query.sql.contains("AND ($reserved OR type NOT LIKE ? ESCAPE '\\')"))
        val tail = query.params.takeLast(3)
        assertEquals(listOf<Any>("%bill%", "%pay%", "%debug%"), tail)
    }

    @Test
    fun `the wildcards of the tag rules are escaped`() {
        val query = taggedLogs(TimelineFilterDomainModel(logTagRules = listOf(TimelineRuleDomainModel("a%b_", exclude = false))))
        assertEquals("%a\\%b\\_%", query.params.last())
    }

    @Test
    fun `the tag rules are not applied to count the types`() {
        val counts = buildTimelineFacetCountsQuery(
            "device",
            "com.app",
            TimelineFilterDomainModel(logTagRules = listOf(TimelineRuleDomainModel("bill", exclude = false))),
        )
        // applied to the two level counts, not to the types
        assertEquals(2, counts.params.count { it == "%bill%" })
    }

    @Test
    fun `errorLevelRowHasError`() {
        assertTrue(logs().sql.contains("CASE WHEN level = 'ERROR' THEN 1 ELSE 0 END AS hasError"))
    }

    @Test
    fun `customTagAppearsAsLogTypeFacetValueAndFilters`() {
        // the custom tags are not known values: they are counted from the rows
        assertFalse(TimelineFacet.LogType.knownValues.contains("billing"))
        val counts = buildTimelineFacetCountsQuery(
            deviceId = "device",
            packageName = "com.app",
            filter = TimelineFilterDomainModel(),
        )
        assertTrue(counts.sql.contains("SELECT 'LogType' AS facet, facetLogType AS value, COUNT(*) AS count FROM ("))
        assertTrue(counts.sql.contains("SELECT 'LogLevel' AS facet, facetLogLevel AS value"))

        val filtered = logs(
            TimelineFilterDomainModel(
                sources = setOf(TimelineSource.Log),
                facets = mapOf(TimelineFacet.LogType to setOf("billing")),
            ),
        )
        assertTrue(filtered.params.contains("billing"))
    }

    @Test
    fun `emptyValueStillProducesRow`() {
        // no filter on the value: only the text filter, when used, looks at it
        val sql = logs().sql
        assertFalse(sql.contains("value IS NOT NULL"))
        assertFalse(sql.contains("value <> ''"))
        assertFalse(sql.contains("LIKE"))
    }

    @Test
    fun `sameTimestampBothRowsReturnedInStableOrder`() {
        // rows of the same time are told apart by their id, so the order is the same on every query
        assertTrue(logs().sql.endsWith("u.id DESC"))
    }

    @Test
    fun `time bounds add a where on the event time`() {
        val query = build(TimelineFilterDomainModel(fromMs = 100L, toMs = 200L))

        assertTrue(query.sql.contains(") u WHERE u.ts >= ? AND u.ts <= ? ORDER BY"))
        assertEquals(listOf<Any>(100L, 200L), query.params.takeLast(2))
    }

    @Test
    fun `only the bounds that are set are applied`() {
        val query = build(TimelineFilterDomainModel(toMs = 200L))

        assertTrue(query.sql.contains(") u WHERE u.ts <= ? ORDER BY"))
        assertFalse(query.sql.contains("u.ts >= ?"))
        assertEquals(200L, query.params.last())
    }

    @Test
    fun `an event list restricts to the kind and id pairs, a call is its request event`() {
        val query = build(
            TimelineFilterDomainModel(
                events = setOf(
                    TimelineEventRef(TimelineSource.Network, "abc"),
                    TimelineEventRef(TimelineSource.Crash, "7"),
                ),
            )
        )

        assertTrue(query.sql.contains(") u WHERE ((u.kind = ? AND u.id = ?) OR (u.kind = ? AND u.id = ?)) ORDER BY"))
        assertEquals(listOf<Any>(KIND_NETWORK, "abc", KIND_CRASH, "7"), query.params.takeLast(4))
    }

    @Test
    fun `an empty event list matches nothing`() {
        val query = build(TimelineFilterDomainModel(events = emptySet()))

        assertTrue(query.sql.contains(") u WHERE 0 ORDER BY"))
    }

    @Test
    fun `bounds and events are both applied`() {
        val query = build(
            TimelineFilterDomainModel(
                fromMs = 1L,
                toMs = 2L,
                events = setOf(TimelineEventRef(TimelineSource.Analytics, "x")),
            )
        )

        assertTrue(query.sql.contains(") u WHERE u.ts >= ? AND u.ts <= ? AND ((u.kind = ? AND u.id = ?)) ORDER BY"))
        assertEquals(listOf<Any>(1L, 2L, KIND_ANALYTICS, "x"), query.params.takeLast(4))
    }

    @Test
    fun `without bounds nor events the query has no outer where`() {
        val query = build()

        assertFalse(query.sql.contains(") u WHERE"))
    }
}
