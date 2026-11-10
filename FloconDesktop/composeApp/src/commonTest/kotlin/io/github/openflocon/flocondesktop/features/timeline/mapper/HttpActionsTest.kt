package io.github.openflocon.flocondesktop.features.timeline.mapper

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class HttpActionsTest {

    @Test
    fun `an http call has the http actions`() {
        assertTrue(hasHttpActions("GET"))
        assertTrue(hasHttpActions("POST"))
        assertTrue(hasHttpActions(null))
    }

    @Test
    fun `a gRPC or a WebSocket call has not`() {
        assertFalse(hasHttpActions("GRPC"))
        assertFalse(hasHttpActions("WS"))
    }
}
