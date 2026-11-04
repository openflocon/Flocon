package io.github.openflocon.data.core.timeline.repository

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.timeline.models.TimelineEventRef
import io.github.openflocon.domain.timeline.models.TimelineFocusRequestDomainModel
import io.github.openflocon.domain.timeline.models.TimelineSource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TimelineFocusRepositoryImplTest {

    private var time = 1_000L
    private val repository = TimelineFocusRepositoryImpl { time }

    private val target = DeviceIdAndPackageNameDomainModel(deviceId = "pixel", packageName = "io.flocon.sample", appInstance = 1L)

    private fun request(fromMs: Long = 10L, event: TimelineEventRef? = null) = repository.requestFocus(target = target, sessionStart = 5L, event = event, fromMs = fromMs, toMs = fromMs + 100)

    private fun pending(): TimelineFocusRequestDomainModel? = runBlocking {
        withTimeoutOrNull(100) { repository.observeFocus().first() }
    }

    @Test
    fun `a request becomes pending with everything the timeline needs`() {
        val event = TimelineEventRef(TimelineSource.Crash, "7")

        request(fromMs = 10L, event = event)

        val pending = pending()!!
        assertEquals(target, pending.target)
        assertEquals(5L, pending.sessionStart)
        assertEquals(event, pending.event)
        assertEquals(10L, pending.fromMs)
        assertEquals(110L, pending.toMs)
    }

    @Test
    fun `nothing is emitted without a request`() {
        assertNull(pending())
    }

    @Test
    fun `a newer request replaces the pending one`() {
        request(fromMs = 10L)
        time += 10
        request(fromMs = 500L)

        assertEquals(500L, pending()?.fromMs)
    }

    @Test
    fun `consuming the pending request clears it`() {
        request()

        repository.consumeFocus(pending()!!)

        assertNull(pending())
    }

    @Test
    fun `consuming an older request does not clear a newer one`() {
        request(fromMs = 10L)
        val older = pending()!!
        time += 10
        request(fromMs = 500L)

        repository.consumeFocus(older)

        assertEquals(500L, pending()?.fromMs)
    }

    @Test
    fun `a request older than the ttl is not emitted anymore`() {
        request()

        time += TIMELINE_FOCUS_TTL_MS + 1

        assertNull(pending())
    }

    @Test
    fun `a request exactly at the ttl is still emitted`() {
        request()

        time += TIMELINE_FOCUS_TTL_MS

        assertEquals(10L, pending()?.fromMs)
    }
}
