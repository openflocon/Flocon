package io.github.openflocon.domain.common.time

import kotlin.test.Test
import kotlin.test.assertEquals

class FormatDurationTest {

    @Test
    fun `under a second stays in milliseconds`() {
        assertEquals("212ms", formatDuration(212.0))
        assertEquals("999ms", formatDuration(999.0))
    }

    @Test
    fun `seconds drop a null decimal`() {
        assertEquals("1s", formatDuration(1_000.0))
        assertEquals("1.2s", formatDuration(1_204.0))
        assertEquals("10s", formatDuration(10_000.0))
        assertEquals("59.9s", formatDuration(59_900.0))
    }

    @Test
    fun `minutes`() {
        assertEquals("1m", formatDuration(60_000.0))
        assertEquals("1m 5s", formatDuration(65_000.0))
        assertEquals("12m 30s", formatDuration(750_000.0))
    }
}
