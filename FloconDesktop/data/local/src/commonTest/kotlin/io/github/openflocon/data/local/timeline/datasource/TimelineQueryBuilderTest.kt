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

    @Test
    fun `every source is merged by a union sorted from the newest`() {
        val query = build()

        assertEquals(TimelineSource.entries.size - 1, Regex("UNION ALL").findAll(query.sql).count())
        assertTrue(query.sql.contains("FROM FloconNetworkCallEntity"))
        assertTrue(query.sql.contains("FROM DatabaseQueryLogEntity"))
        assertTrue(query.sql.contains("FROM AnalyticsItemEntity"))
        assertTrue(query.sql.contains("FROM CrashReportEntity"))
        assertTrue(query.sql.contains("FROM TableItemEntity JOIN TableEntity"))
        assertTrue(query.sql.contains("FROM DeeplinkEntity"))
        assertTrue(query.sql.contains("FROM AdbCommandHistoryEntity"))
        assertTrue(query.sql.endsWith("ORDER BY ts DESC, id DESC"))
    }

    @Test
    fun `only the opened deeplinks are events, not the ones declared by the app`() {
        val query = build(TimelineFilterDomainModel(sources = setOf(TimelineSource.Deeplink)))

        assertTrue(query.sql.contains("isHistory = 1"))
        assertEquals(listOf<Any>("device", "com.app"), query.params)
    }

    @Test
    fun `only the selected sources are queried`() {
        val query = build(TimelineFilterDomainModel(sources = setOf(TimelineSource.Crash)))

        assertFalse(query.sql.contains("UNION ALL"))
        assertFalse(query.sql.contains("FloconNetworkCallEntity"))
        assertTrue(query.sql.contains("FROM CrashReportEntity"))
        assertEquals(listOf<Any>("device", "com.app"), query.params)
    }

    @Test
    fun `the app starts are the sessions found in the network, database and analytics tables`() {
        val query = build(TimelineFilterDomainModel(sources = setOf(TimelineSource.AppStart)))

        assertTrue(query.sql.contains("SELECT appInstance FROM FloconNetworkCallEntity"))
        assertTrue(query.sql.contains("SELECT appInstance FROM DatabaseQueryLogEntity"))
        assertTrue(query.sql.contains("SELECT appInstance FROM AnalyticsItemEntity"))
        assertEquals(3, Regex(" UNION SELECT").findAll(query.sql).count() + 1)
        assertEquals(listOf<Any>("device", "com.app", "device", "com.app", "device", "com.app"), query.params)
    }

    @Test
    fun `the session filter keeps only the start of that session`() {
        val query = build(TimelineFilterDomainModel(sources = setOf(TimelineSource.AppStart), appInstance = 42L))

        assertTrue(query.sql.contains(" WHERE appInstance = ?"))
        assertEquals(42L, query.params.last())
    }

    @Test
    fun `the app starts are hidden while filtering on a text`() {
        val query = build(TimelineFilterDomainModel(sources = setOf(TimelineSource.AppStart), text = "login"))

        assertTrue(query.sql.endsWith("WHERE 0"))
    }

    @Test
    fun `the adb commands are only scoped by the device`() {
        val query = build(TimelineFilterDomainModel(sources = setOf(TimelineSource.Adb)))

        assertFalse(query.sql.contains("packageName"))
        assertEquals(listOf<Any>("device"), query.params)
    }

    @Test
    fun `the database logs are limited to the most recent ones`() {
        val query = build(TimelineFilterDomainModel(sources = setOf(TimelineSource.Database)))

        assertTrue(query.sql.contains("ORDER BY timestamp DESC LIMIT $MAX_DATABASE_LOGS"))
    }

    @Test
    fun `the text filter is applied on every source`() {
        val query = build(TimelineFilterDomainModel(text = "login"))

        // every source but the app starts (nothing to search in) filters on two columns
        val expected = (TimelineSource.entries.size - 1) * 2
        assertEquals(expected, Regex("LIKE \\?").findAll(query.sql).count())
        assertEquals(expected, query.params.count { it == "%login%" })
    }

    @Test
    fun `the session filter is only applied on the sources that store it`() {
        val query = build(TimelineFilterDomainModel(appInstance = 42L))

        // network, database, analytics and the app starts
        assertEquals(4, Regex("appInstance = \\?").findAll(query.sql).count())
        assertEquals(4, query.params.count { it == 42L })
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
}
