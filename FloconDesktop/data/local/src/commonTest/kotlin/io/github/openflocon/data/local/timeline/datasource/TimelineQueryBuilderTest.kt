package io.github.openflocon.data.local.timeline.datasource

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

    @Test
    fun `every source is merged by a union sorted from the newest`() {
        val query = build()

        assertEquals(eventSources - 1, Regex("UNION ALL").findAll(query.sql).count())
        assertTrue(query.sql.contains("FROM FloconNetworkCallEntity"))
        assertTrue(query.sql.contains("FROM DatabaseQueryLogEntity"))
        assertTrue(query.sql.contains("FROM AnalyticsItemEntity"))
        assertTrue(query.sql.contains("FROM CrashReportEntity"))
        assertTrue(query.sql.contains("FROM TableItemEntity JOIN TableEntity"))
        assertTrue(query.sql.contains("FROM DeeplinkEntity"))
        assertTrue(query.sql.contains("FROM AdbCommandHistoryEntity"))
        assertTrue(query.sql.endsWith("ORDER BY u.ts DESC, u.id DESC"))
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
    fun `the app starts are not events`() {
        val query = build(TimelineFilterDomainModel(sources = setOf(TimelineSource.AppStart)))

        assertTrue(query.sql.endsWith("WHERE 0"))
        assertTrue(query.params.isEmpty())
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
        assertEquals(eventSources * 2, Regex("LIKE \\?").findAll(query.sql).count())
        assertEquals(eventSources * 2, query.params.count { it == "%login%" })
    }

    @Test
    fun `the session filter is only applied on the sources that store it`() {
        val query = build(TimelineFilterDomainModel(appInstance = 42L))

        // network, database and analytics
        assertEquals(3, Regex("appInstance = \\?").findAll(query.sql).count())
        assertEquals(3, query.params.count { it == 42L })
    }

    @Test
    fun `a blank text is ignored`() {
        val query = build(TimelineFilterDomainModel(text = "   "))

        assertFalse(query.sql.contains("LIKE"))
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
    fun `the session counts follow the selected events`() {
        val query = buildTimelineSessionCountsQuery(
            deviceId = "device",
            packageName = "com.app",
            filter = TimelineFilterDomainModel(sources = setOf(TimelineSource.Crash)),
        )

        assertTrue(query.sql.contains("GROUP BY sessionStart"))
        assertTrue(query.sql.contains("FROM CrashReportEntity"))
        assertFalse(query.sql.contains("request_url AS title"))
    }
}
