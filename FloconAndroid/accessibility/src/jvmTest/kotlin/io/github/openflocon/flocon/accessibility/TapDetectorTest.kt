package io.github.openflocon.flocon.accessibility

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TapDetectorTest {

    private val detector = TapDetector(touchSlop = 8f, maxDurationMs = 400)

    @Test
    fun `quick down and up on the same point is a tap`() {
        detector.onDown(100f, 100f, time = 0)
        assertTrue(detector.onUp(100f, 100f, time = 80))
    }

    @Test
    fun `small movement inside the slop is still a tap`() {
        detector.onDown(100f, 100f, time = 0)
        detector.onMove(104f, 103f)
        assertTrue(detector.onUp(104f, 103f, time = 120))
    }

    @Test
    fun `movement beyond the slop is a drag`() {
        detector.onDown(100f, 100f, time = 0)
        detector.onMove(100f, 160f)
        assertFalse(detector.onUp(100f, 160f, time = 100))
    }

    @Test
    fun `a drag that comes back to the start is not a tap`() {
        detector.onDown(100f, 100f, time = 0)
        detector.onMove(100f, 200f)
        detector.onMove(100f, 100f)
        assertFalse(detector.onUp(100f, 100f, time = 100))
    }

    @Test
    fun `up far from the down point is not a tap`() {
        detector.onDown(100f, 100f, time = 0)
        assertFalse(detector.onUp(200f, 100f, time = 100))
    }

    @Test
    fun `long press is not a tap`() {
        detector.onDown(100f, 100f, time = 0)
        assertFalse(detector.onUp(100f, 100f, time = 401))
    }

    @Test
    fun `up without down is not a tap`() {
        assertFalse(detector.onUp(100f, 100f, time = 10))
    }

    @Test
    fun `cancelled gesture is not a tap`() {
        detector.onDown(100f, 100f, time = 0)
        detector.cancel()
        assertFalse(detector.onUp(100f, 100f, time = 50))
    }

    @Test
    fun `a gesture is consumed once`() {
        detector.onDown(100f, 100f, time = 0)
        assertTrue(detector.onUp(100f, 100f, time = 50))
        assertFalse(detector.onUp(100f, 100f, time = 60))
    }
}
