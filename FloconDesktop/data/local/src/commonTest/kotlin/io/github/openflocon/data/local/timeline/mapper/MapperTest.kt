package io.github.openflocon.data.local.timeline.mapper

import io.github.openflocon.data.local.timeline.models.TimelineRowEntity
import io.github.openflocon.data.local.timeline.models.TimelineSessionCountEntity
import io.github.openflocon.data.local.timeline.models.TimelineSummaryRowEntity
import io.github.openflocon.domain.timeline.models.TimelinePhase
import io.github.openflocon.domain.timeline.models.TimelineSource
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MapperTest {

    @Test
    fun `a row carries the details of a network call and its session`() {
        val event = TimelineRowEntity(
            kind = "network",
            id = "c1",
            ts = 1100L,
            title = "https://a/login",
            subtitle = "200",
            appInstance = 1000L,
            method = "POST",
            statusCode = 200,
            durationMs = 120.5,
            sessionStart = 1000L,
        ).toDomain()

        assertEquals(TimelineSource.Network, event.source)
        assertEquals("POST", event.method)
        assertEquals(200, event.statusCode)
        assertEquals(120.5, event.durationMs)
        assertEquals(1000L, event.sessionStart)
        assertEquals(TimelinePhase.Request, event.phase)
    }

    @Test
    fun `the response of a network call is a network event of the response phase`() {
        val event = TimelineRowEntity(
            kind = "network_response",
            id = "c1",
            ts = 1220L,
            title = "https://a/login",
            subtitle = "200",
            appInstance = 1000L,
            method = "POST",
            statusCode = 200,
            durationMs = 120.5,
            sessionStart = 1000L,
        ).toDomain()

        assertEquals(TimelineSource.Network, event.source)
        assertEquals(TimelinePhase.Response, event.phase)
        assertEquals("c1", event.id)
    }

    @Test
    fun `an event without session or network details has none`() {
        val event = TimelineRowEntity(
            kind = "crash",
            id = "k1",
            ts = 3500L,
            title = "NullPointerException",
            subtitle = "boom",
            appInstance = null,
            method = null,
            statusCode = null,
            durationMs = null,
            sessionStart = null,
        ).toDomain()

        assertEquals(TimelineSource.Crash, event.source)
        assertNull(event.sessionStart)
        assertNull(event.statusCode)
    }

    @Test
    fun `an accessibility row is an accessibility event`() {
        val event = TimelineRowEntity(
            kind = "accessibility",
            id = "a1",
            ts = 1200L,
            title = "Add to cart",
            subtitle = "VIEW_FOCUSED",
            appInstance = 1000L,
            method = null,
            statusCode = null,
            durationMs = null,
            sessionStart = 1000L,
        ).toDomain()

        assertEquals(TimelineSource.Accessibility, event.source)
        assertEquals("VIEW_FOCUSED", event.subtitle)
    }

    @Test
    fun `a webview row is a webview event`() {
        val event = TimelineRowEntity(
            kind = "webview",
            id = "w1",
            ts = 1200L,
            title = "x is undefined",
            subtitle = "error",
            appInstance = null,
            method = null,
            statusCode = null,
            durationMs = null,
            sessionStart = 1000L,
        ).toDomain()

        assertEquals(TimelineSource.WebView, event.source)
        assertEquals("error", event.subtitle)
    }

    @Test
    fun `the summary is indexed by source and by session`() {
        val summary = toSummaryDomain(
            summaryRows = listOf(
                TimelineSummaryRowEntity(kind = "network", count = 4, errors = 2, firstTs = 1100L, lastTs = 3450L),
                TimelineSummaryRowEntity(kind = "appstart", count = 2, errors = 0, firstTs = 1000L, lastTs = 3000L),
            ),
            sessionRows = listOf(TimelineSessionCountEntity(sessionStart = 3000L, count = 6)),
        )

        assertEquals(4, summary.sources.getValue(TimelineSource.Network).count)
        assertEquals(2, summary.sources.getValue(TimelineSource.Network).httpErrorCount)
        assertEquals(2, summary.sources.getValue(TimelineSource.AppStart).count)
        assertEquals(mapOf(3000L to 6), summary.sessionEventCounts)
    }

    @Test
    fun `a row tells if the response of the call is an image`() {
        val row = TimelineRowEntity(
            kind = "network_response",
            id = "c1",
            ts = 1100L,
            title = "https://a/logo.png",
            subtitle = "200",
            appInstance = 1000L,
            method = "GET",
            statusCode = 200,
            durationMs = 20.0,
            sessionStart = 1000L,
            isImage = true,
        )

        assertTrue(row.toDomain().isImage)
        assertFalse(row.copy(isImage = false).toDomain().isImage)
    }
}
