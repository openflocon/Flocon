package io.github.openflocon.flocondesktop.features.timeline

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.timeline.models.TimelineEventRef
import io.github.openflocon.domain.timeline.models.TimelineFocusRequestDomainModel
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.flocondesktop.features.timeline.graph.GraphFocusUiModel
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TimelineFocusTest {

    private val pixel = DeviceIdAndPackageNameDomainModel(deviceId = "pixel", packageName = "io.flocon.sample", appInstance = 1L)
    private val tablet = DeviceIdAndPackageNameDomainModel(deviceId = "tablet", packageName = "io.flocon.sample", appInstance = 9L)

    private fun request(
        target: DeviceIdAndPackageNameDomainModel = pixel,
        sessionStart: Long? = 1_000L,
        event: TimelineEventRef? = TimelineEventRef(TimelineSource.Crash, "7"),
        fromMs: Long = 11_000L,
        toMs: Long = 21_000L,
        requestedAtMs: Long = 5L,
    ) = TimelineFocusRequestDomainModel(target, sessionStart, event, fromMs, toMs, requestedAtMs)

    @Test
    fun `the event is selected with the key of the rows`() {
        val plan = planFocus(request(), sessionStarts = listOf(1_000L), graphShown = true)

        assertEquals("Crash:7", plan.selectedKey)
    }

    @Test
    fun `without event only the zone is shown`() {
        val plan = planFocus(request(event = null), sessionStarts = listOf(1_000L), graphShown = true)

        assertNull(plan.selectedKey)
        assertEquals(GraphFocusUiModel(fromMs = 10_000L, toMs = 20_000L, eventKey = null, token = 5L), plan.graph?.focus)
    }

    @Test
    fun `the zone is told since the start of the session`() {
        val plan = planFocus(request(), sessionStarts = listOf(1_000L), graphShown = true)

        assertEquals(GraphFocusUiModel(fromMs = 10_000L, toMs = 20_000L, eventKey = "Crash:7", token = 5L), plan.graph?.focus)
    }

    @Test
    fun `the latest session is the one that follows the new ones, an older one is chosen`() {
        assertNull(planFocus(request(sessionStart = 3_000L), sessionStarts = listOf(1_000L, 3_000L), graphShown = true).graph?.session)
        assertEquals(1_000L, planFocus(request(sessionStart = 1_000L), sessionStarts = listOf(1_000L, 3_000L), graphShown = true).graph?.session)
    }

    @Test
    fun `a session that is not displayed falls back to the latest one`() {
        // hidden by the filters (ex: current session only): the graph shows the latest session
        val plan = planFocus(request(sessionStart = 1_000L, fromMs = 13_000L, toMs = 14_000L), sessionStarts = listOf(3_000L), graphShown = true)

        assertNull(plan.graph?.session)
        assertEquals(10_000L, plan.graph?.focus?.fromMs)
    }

    @Test
    fun `without any session there is no zone to show`() {
        val plan = planFocus(request(sessionStart = null), sessionStarts = emptyList(), graphShown = true)

        assertNull(plan.graph?.focus)
        assertNull(plan.graph?.session)
        assertEquals("Crash:7", plan.selectedKey)
    }

    @Test
    fun `in list view only the row is selected, the graph is left alone`() {
        val plan = planFocus(request(), sessionStarts = listOf(1_000L, 3_000L), graphShown = false)

        assertEquals("Crash:7", plan.selectedKey)
        assertNull(plan.graph)
    }

    @Test
    fun `a request is applied once its device is the one displayed`() = runBlocking {
        val current = MutableStateFlow<DeviceIdAndPackageNameDomainModel?>(tablet)
        val applied = async(start = CoroutineStart.UNDISPATCHED) { appliedFocusRequests(flowOf(request()), current).toList() }

        current.value = pixel.copy(appInstance = 2L)

        assertEquals(listOf(request()), withTimeoutOrNull(1_000) { applied.await() })
    }

    @Test
    fun `a request waiting for its device is replaced by the next one`() = runBlocking {
        val current = MutableStateFlow<DeviceIdAndPackageNameDomainModel?>(null)
        val requests = MutableStateFlow(request(requestedAtMs = 1L))
        val applied = async(start = CoroutineStart.UNDISPATCHED) { appliedFocusRequests(requests, current).first() }

        requests.value = request(requestedAtMs = 2L)
        current.value = pixel

        assertEquals(2L, withTimeoutOrNull(1_000) { applied.await() }?.requestedAtMs)
    }

    @Test
    fun `a request for another device is not applied`() = runBlocking {
        val current = MutableStateFlow<DeviceIdAndPackageNameDomainModel?>(tablet)

        val applied = withTimeoutOrNull(100) { appliedFocusRequests(flowOf(request(target = pixel)), current).first() }

        assertNull(applied)
    }
}
