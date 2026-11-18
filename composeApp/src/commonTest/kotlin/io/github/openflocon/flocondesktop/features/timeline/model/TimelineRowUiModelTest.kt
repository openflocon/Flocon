package io.github.openflocon.flocondesktop.features.timeline.model

import io.github.openflocon.domain.timeline.models.TimelinePhase
import io.github.openflocon.domain.timeline.models.TimelineSource
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class TimelineRowUiModelTest {

    private fun row(source: TimelineSource, id: String, phase: TimelinePhase = TimelinePhase.Request) = TimelineRowUiModel(
        id = id,
        source = source,
        day = "20/10/2026",
        time = "12:00:00.000",
        timestamp = 0L,
        title = "title",
        subtitle = null,
        phase = phase,
    )

    @Test
    fun `the request and the response of a call are two rows of the same call`() {
        val request = row(TimelineSource.Network, id = "call-1")
        val response = row(TimelineSource.Network, id = "call-1", phase = TimelinePhase.Response)

        assertNotEquals(request.key, response.key)
        assertEquals(request.callKey, response.callKey)
    }

    @Test
    fun `the key of a request stays the one of its call`() {
        assertEquals("Network:call-1", row(TimelineSource.Network, id = "call-1").key)
    }

    @Test
    fun `two sources with the same id are two calls`() {
        assertNotEquals(
            row(TimelineSource.Network, id = "1").callKey,
            row(TimelineSource.Database, id = "1").callKey,
        )
    }
}
