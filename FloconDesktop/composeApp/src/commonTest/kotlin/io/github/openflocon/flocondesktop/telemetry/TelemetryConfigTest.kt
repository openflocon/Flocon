package io.github.openflocon.flocondesktop.telemetry

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TelemetryConfigTest {
    @Test
    fun `do not track parsing`() {
        assertFalse(isDoNotTrack(null))
        assertFalse(isDoNotTrack(""))
        assertFalse(isDoNotTrack("0"))
        assertFalse(isDoNotTrack("false"))
        assertTrue(isDoNotTrack("1"))
        assertTrue(isDoNotTrack("true"))
    }
}
