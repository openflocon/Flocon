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
        runDistance = 400f,
        runSpeedPerSecond = 300f,
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
    fun doesNotTurnAroundMoreOftenThanTheMinimumInterval() {
        val mascot = state()
        mascot.update(16, 0, 100f, 0f, 1_000f)
        assertFalse(mascot.facingRight)

        // the pointer jumps to the other side right away: it keeps its direction for now
        mascot.update(16, 16, 900f, 0f, 1_000f)
        assertFalse(mascot.facingRight)

        mascot.run(targetX = 900f, durationMs = 700, startMs = 32)
        assertTrue(mascot.facingRight)
    }

    @Test
    fun fallsAsleepAfterBeingIdle() {
        val mascot = state()
        mascot.run(targetX = 500f, durationMs = 5_000)
        assertEquals(FloconMascotMode.Sleep, mascot.mode)
    }

    @Test
    fun wakesUpWhenThePointerGoesFarAway() {
        val mascot = state()
        mascot.run(targetX = 500f, durationMs = 5_000)
        mascot.update(16, 5_000, 900f, 0f, 1_000f)
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

    @Test
    fun beingPettedMakesItHappyThenSeatedAgain() {
        val mascot = state()
        mascot.pet()
        mascot.update(16, 0, 500f, 0f, 1_000f)
        assertEquals(FloconMascotMode.Happy, mascot.mode)

        mascot.run(targetX = 500f, durationMs = 1_800, startMs = 16)
        assertEquals(FloconMascotMode.Sit, mascot.mode)
    }

    @Test
    fun beingPettedWakesItUpAndKeepsItInPlace() {
        val mascot = state()
        mascot.run(targetX = 500f, durationMs = 5_000)
        assertEquals(FloconMascotMode.Sleep, mascot.mode)

        mascot.pet()
        mascot.update(16, 5_000, 900f, 0f, 1_000f)
        assertEquals(FloconMascotMode.Happy, mascot.mode)
        assertEquals(500f, mascot.x)
    }

    @Test
    fun hopsWhileHappy() {
        val mascot = state()
        mascot.pet()
        val seen = mutableSetOf<Boolean>()
        var t = 0L
        while (t < 800) {
            mascot.update(16, t, 500f, 0f, 1_000f)
            seen += mascot.hopUp
            t += 16
        }
        assertEquals(setOf(true, false), seen)
    }

    @Test
    fun runsFasterWhenThePointerIsVeryFar() {
        val near = state(startX = 100f)
        val far = state(startX = 100f)
        near.run(targetX = 400f, durationMs = 500)
        far.run(targetX = 900f, durationMs = 500)

        assertFalse(near.running)
        assertTrue(far.running)
        assertTrue(far.x - 100f > (near.x - 100f) * 2)
    }

    @Test
    fun walksAgainOnceTheRunBroughtItCloseEnough() {
        val mascot = state(startX = 100f)
        mascot.run(targetX = 900f, durationMs = 2_600)
        assertFalse(mascot.running)
        assertEquals(FloconMascotMode.Walk, mascot.mode)
    }

    @Test
    fun liesDownAfterALongTime() {
        val mascot = state()
        mascot.run(targetX = 500f, durationMs = 9_000)
        assertEquals(FloconMascotMode.Lie, mascot.mode)
    }

    @Test
    fun staysLyingForAtLeastTheMinimumDurationBeforeWakingUp() {
        val mascot = state()
        mascot.run(targetX = 500f, durationMs = 9_000)
        assertEquals(FloconMascotMode.Lie, mascot.mode)

        // it has been lying for about a second: a far pointer doesn't wake it yet
        mascot.run(targetX = 900f, durationMs = 4_000, startMs = 9_000)
        assertEquals(FloconMascotMode.Lie, mascot.mode)

        mascot.run(targetX = 900f, durationMs = 2_500, startMs = 13_000)
        assertEquals(FloconMascotMode.Walk, mascot.mode)
    }

    private fun FloconMascotState.stroke(swings: Int, swingMs: Long, startMs: Long = 0, length: Float = 20f) {
        var t = startMs
        var x = 0f
        onHover(x, t, minStroke = 6f)
        repeat(swings) { index ->
            t += swingMs
            x += if (index % 2 == 0) length else -length
            onHover(x, t, minStroke = 6f)
        }
    }

    @Test
    fun strokingItBackAndForthMakesItPurr() {
        val mascot = state()
        mascot.stroke(swings = 6, swingMs = 100)
        mascot.update(16, 600, 500f, 0f, 1_000f)
        assertEquals(FloconMascotMode.Petted, mascot.mode)
    }

    @Test
    fun aSingleSwingIsNotACaress() {
        val mascot = state()
        mascot.stroke(swings = 2, swingMs = 100)
        mascot.update(16, 200, 500f, 0f, 1_000f)
        assertEquals(FloconMascotMode.Sit, mascot.mode)
    }

    @Test
    fun slowSwingsAreNotACaress() {
        val mascot = state()
        mascot.stroke(swings = 8, swingMs = 1_000)
        mascot.update(16, 8_000, 500f, 0f, 1_000f)
        assertEquals(FloconMascotMode.Sit, mascot.mode)
    }

    @Test
    fun tinyWigglesAreNotACaress() {
        val mascot = state()
        mascot.stroke(swings = 8, swingMs = 100, length = 2f)
        mascot.update(16, 800, 500f, 0f, 1_000f)
        assertEquals(FloconMascotMode.Sit, mascot.mode)
    }

    @Test
    fun stopsPurringShortlyAfterTheCaressEnds() {
        val mascot = state()
        mascot.stroke(swings = 6, swingMs = 100)
        mascot.update(16, 600, 500f, 0f, 1_000f)
        assertEquals(FloconMascotMode.Petted, mascot.mode)

        mascot.run(targetX = 500f, durationMs = 1_200, startMs = 616)
        assertEquals(FloconMascotMode.Sit, mascot.mode)
    }
}
