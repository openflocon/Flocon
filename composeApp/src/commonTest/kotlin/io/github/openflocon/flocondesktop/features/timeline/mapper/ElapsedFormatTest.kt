package io.github.openflocon.flocondesktop.features.timeline.mapper

import kotlin.test.Test
import kotlin.test.assertEquals

class ElapsedFormatTest {

    @Test
    fun `below a second the duration is in milliseconds`() {
        assertEquals("0 ms", formatElapsed(0.0))
        assertEquals("488 ms", formatElapsed(488.0))
        assertEquals("121 ms", formatElapsed(120.5))
    }

    @Test
    fun `below a minute the duration is in seconds with two decimals`() {
        assertEquals("1.70 s", formatElapsed(1_700.0))
        assertEquals("1.05 s", formatElapsed(1_050.0))
        assertEquals("12.00 s", formatElapsed(12_000.0))
    }

    @Test
    fun `from a minute the duration has minutes and seconds`() {
        assertEquals("1 min 03 s", formatElapsed(63_000.0))
        assertEquals("12 min 00 s", formatElapsed(720_000.0))
    }

    @Test
    fun `a delta starts with a plus and is never negative`() {
        assertEquals("+488 ms", formatDelta(488))
        assertEquals("+1.70 s", formatDelta(1_700))
        assertEquals("+0 ms", formatDelta(-5))
    }
}
