package io.github.openflocon.library.designsystem.components

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FloconMascotStateTest {

    private val config = FloconMascotConfig(
        stopDistance = 30f,
        wakeDistance = 100f,
        speedPerSecond = 100f,
    )

    private fun state(startX: Float = 500f) = FloconMascotState(startX, config)

    private fun FloconMascotState.run(targetX: Float, durationMs: Long, startMs: Long = 0, frameMs: Long = 16) {
        var t = 0L
        while (t < durationMs) {
            update(frameMs, startMs + t, targetX, minX = 0f, maxX = 1_000f)
            t += frameMs
        }
    }

    @Test
    fun staysSeatedWhileThePointerIsNear() {
        val mascot = state()
        mascot.run(targetX = 560f, durationMs = 2_000)
        assertEquals(500f, mascot.x)
        assertEquals(FloconMascotMode.Sit, mascot.mode)
    }

    @Test
    fun walksTowardsAFarPointerAndStopsNextToIt() {
        val mascot = state()
        mascot.update(16, 0, 800f, 0f, 1_000f)
        assertEquals(FloconMascotMode.Walk, mascot.mode)
        assertTrue(mascot.facingRight)

        mascot.run(targetX = 800f, durationMs = 6_000, startMs = 16)
        assertEquals(770f, mascot.x, absoluteTolerance = 0.5f)
        assertEquals(FloconMascotMode.Sit, mascot.mode)
    }

    @Test
    fun facesLeftWhenThePointerIsOnTheLeft() {
        val mascot = state()
        mascot.update(16, 0, 100f, 0f, 1_000f)
        assertFalse(mascot.facingRight)
    }

    @Test
    fun fallsAsleepAfterBeingIdle() {
        val mascot = state()
        mascot.run(targetX = 500f, durationMs = 9_000)
        assertEquals(FloconMascotMode.Sleep, mascot.mode)
    }

    @Test
    fun wakesUpWhenThePointerGoesFarAway() {
        val mascot = state()
        mascot.run(targetX = 500f, durationMs = 9_000)
        mascot.update(16, 9_000, 900f, 0f, 1_000f)
        assertEquals(FloconMascotMode.Walk, mascot.mode)
    }

    @Test
    fun staysSeatedForAtLeastTheMinimumDurationBeforeLeavingAgain() {
        val mascot = state()
        // it needs 2.7 s to get next to the pointer, so it has been seated for less than a second
        mascot.run(targetX = 800f, durationMs = 3_500)
        assertEquals(FloconMascotMode.Sit, mascot.mode)

        // the pointer jumps away right after it sat down: it waits for minSitMs
        mascot.update(16, 3_500, 100f, 0f, 1_000f)
        assertEquals(FloconMascotMode.Sit, mascot.mode)
        mascot.run(targetX = 100f, durationMs = 3_100, startMs = 3_516)
        assertEquals(FloconMascotMode.Walk, mascot.mode)
    }

    @Test
    fun staysInsideTheBounds() {
        val mascot = state()
        mascot.update(16, 0, 5_000f, 0f, 600f)
        mascot.run(targetX = 5_000f, durationMs = 6_000, startMs = 16)
        assertTrue(mascot.x <= 1_000f)
    }

    @Test
    fun legsAlternateWhileWalking() {
        val mascot = state()
        val seen = mutableSetOf<Boolean>()
        var t = 0L
        while (t < 1_000) {
            mascot.update(16, t, 900f, 0f, 1_000f)
            seen += mascot.alternateLegs
            t += 16
        }
        assertEquals(setOf(true, false), seen)
    }
}
