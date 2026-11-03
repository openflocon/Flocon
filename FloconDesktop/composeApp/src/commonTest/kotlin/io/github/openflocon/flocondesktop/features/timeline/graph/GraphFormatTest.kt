package io.github.openflocon.flocondesktop.features.timeline.graph

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GraphFormatTest {

    @Test
    fun `durations`() {
        assertEquals("0 ms", formatGraphDuration(0))
        assertEquals("999 ms", formatGraphDuration(999))
        assertEquals("1.00 s", formatGraphDuration(1_000))
        assertEquals("1.21 s", formatGraphDuration(1_210))
        assertEquals("1 min 01 s", formatGraphDuration(61_000))
        assertEquals("1 min 05 s", formatGraphDuration(65_000))
    }

    @Test
    fun `a start has a plus`() {
        assertEquals("+190 ms", formatGraphStart(190))
        assertEquals("+1.32 s", formatGraphStart(1_320))
    }

    @Test
    fun `the step of the axis is 1, 2 or 5 times a power of ten`() {
        val allowed = setOf(1L, 2L, 5L)
        listOf(0.0005, 0.003, 0.1, 0.9, 5.0, 20.0).forEach { pxPerMs ->
            var step = axisStepMs(pxPerMs)
            while (step >= 10) step /= 10
            assertTrue(step in allowed, "step for $pxPerMs")
        }
        // 0.2 px per ms: 100 px is 500 ms
        assertEquals(500L, axisStepMs(0.2))
        assertEquals(1_000L, axisStepMs(0.1))
        assertEquals(20L, axisStepMs(5.0))
    }

    @Test
    fun `axis labels`() {
        assertEquals("0", formatAxisLabel(0, 300))
        assertEquals("200 ms", formatAxisLabel(200, 200))
        assertEquals("1.2 s", formatAxisLabel(1_200, 200))
        assertEquals("3 s", formatAxisLabel(3_000, 1_000))
    }

    @Test
    fun `the time of day has no milliseconds when the ticks are a second apart`() {
        assertEquals("12:00:05", formatClockLabel("12:00:05.123", stepMs = 1_000))
        assertEquals("12:00:05", formatClockLabel("12:00:05.123", stepMs = 5_000))
        assertEquals("12:00:05.123", formatClockLabel("12:00:05.123", stepMs = 500))
    }

    @Test
    fun `what a bar has room for`() {
        assertFalse(showBarIcon(29f))
        assertTrue(showBarIcon(30f))
        assertFalse(showBarChip(71f))
        assertTrue(showBarChip(72f))
        assertFalse(showBarText(35f))
        assertTrue(showBarText(36f))
    }
}
