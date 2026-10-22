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

    // two islands side by side, with a gap of 60 between them
    private val sidebar = FloconMascotPlatform(minX = 0f, maxX = 100f, y = 0f)
    private val content = FloconMascotPlatform(minX = 160f, maxX = 500f, y = 10f)
    private val islands = listOf(sidebar, content)
    private val frameMs = 16L

    private fun FloconMascotState.runOn(
        platforms: List<FloconMascotPlatform>,
        targetX: Float,
        durationMs: Long,
        startMs: Long = 0,
        onFrame: (timeMs: Long) -> Unit = {},
    ) {
        var t = 0L
        while (t < durationMs) {
            update(frameMs, startMs + t, targetX, platforms)
            onFrame(startMs + t)
            t += frameMs
        }
    }

    private fun FloconMascotState.isOnAPlatform(platforms: List<FloconMascotPlatform>) = platforms.any { x >= it.minX - 0.01f && x <= it.maxX + 0.01f }

    @Test
    fun jumpsToTheOtherPlatformWhenThePointerIsOverIt() {
        val mascot = state(startX = 300f)
        mascot.runOn(islands, targetX = 50f, durationMs = 8_000)

        assertTrue(mascot.mode != FloconMascotMode.Jump)
        assertTrue(mascot.x in sidebar.minX..sidebar.maxX)
        assertEquals(sidebar.y, mascot.y)
    }

    @Test
    fun jumpsBackAndForthBetweenPlatforms() {
        val mascot = state(startX = 300f)
        mascot.runOn(islands, targetX = 50f, durationMs = 8_000)
        assertEquals(sidebar.y, mascot.y)

        mascot.runOn(islands, targetX = 400f, durationMs = 8_000, startMs = 8_000)
        assertTrue(mascot.x in content.minX..content.maxX)
        assertEquals(content.y, mascot.y)
    }

    @Test
    fun neverStaysInTheGapNorInTheAir() {
        val mascot = state(startX = 300f)
        var jumpingMs = 0L
        var longestJumpMs = 0L
        var time = 0L
        repeat(30) { round ->
            val target = if (round % 2 == 0) 40f else 450f
            mascot.runOn(islands, targetX = target, durationMs = 1_700, startMs = time) { t ->
                if (mascot.mode == FloconMascotMode.Jump) {
                    jumpingMs += frameMs
                    longestJumpMs = maxOf(longestJumpMs, jumpingMs)
                } else {
                    jumpingMs = 0
                    assertTrue(mascot.isOnAPlatform(islands), "in the gap at $t: x=${mascot.x}")
                }
            }
            time += 1_700
        }
        assertTrue(longestJumpMs <= config.jumpMs + 2 * frameMs, "a jump lasted $longestJumpMs ms")
    }

    @Test
    fun aJumpAlwaysEndsEvenIfThePointerComesBack() {
        val mascot = state(startX = 300f)
        var time = 0L
        // walks to the edge and takes off
        while (mascot.mode != FloconMascotMode.Jump && time < 10_000) {
            mascot.update(frameMs, time, 50f, islands)
            time += frameMs
        }
        assertEquals(FloconMascotMode.Jump, mascot.mode)

        // the pointer goes back to where it was: the jump still goes on to its landing
        var jumpMs = 0L
        while (mascot.mode == FloconMascotMode.Jump && jumpMs < 5_000) {
            mascot.update(frameMs, time, 450f, islands)
            time += frameMs
            jumpMs += frameMs
        }
        assertTrue(jumpMs <= config.jumpMs + 2 * frameMs, "the jump never ended: $jumpMs ms")
        assertTrue(mascot.isOnAPlatform(islands))
    }

    @Test
    fun landsOnAPlatformEvenIfThePlatformsChangeDuringTheJump() {
        val mascot = state(startX = 300f)
        var time = 0L
        while (mascot.mode != FloconMascotMode.Jump && time < 10_000) {
            mascot.update(frameMs, time, 50f, islands)
            time += frameMs
        }
        assertEquals(FloconMascotMode.Jump, mascot.mode)

        // the sidebar collapses to a single spot while it is in the air
        val collapsed = listOf(FloconMascotPlatform(minX = 20f, maxX = 20f, y = 0f), content)
        mascot.runOn(collapsed, targetX = 50f, durationMs = 1_000, startMs = time)
        assertTrue(mascot.isOnAPlatform(collapsed))
        assertTrue(mascot.mode != FloconMascotMode.Jump)
    }

    @Test
    fun clickingDuringAJumpDoesNotInterruptIt() {
        val mascot = state(startX = 300f)
        var time = 0L
        while (mascot.mode != FloconMascotMode.Jump && time < 10_000) {
            mascot.update(frameMs, time, 50f, islands)
            time += frameMs
        }
        mascot.pet()
        mascot.update(frameMs, time, 50f, islands)
        assertEquals(FloconMascotMode.Jump, mascot.mode)
    }

    @Test
    fun staysOnItsPlatformWhenThePointerIsInTheGapNextToIt() {
        val mascot = state(startX = 300f)
        // 140 is in the gap, nearer to the content than to the sidebar
        mascot.runOn(islands, targetX = 140f, durationMs = 6_000)
        assertTrue(mascot.x in content.minX..content.maxX)
        assertFalse(mascot.mode == FloconMascotMode.Jump)
    }

    @Test
    fun backflipsOnTheSpotAndThenSitsAgain() {
        val mascot = state()
        mascot.backflip()
        mascot.update(16, 0, 900f, 0f, 1_000f)
        assertEquals(FloconMascotMode.Backflip, mascot.mode)

        // the pointer is far away but it does not move during the flip
        mascot.runOn(listOf(FloconMascotPlatform(0f, 1_000f, 0f)), targetX = 900f, durationMs = 800, startMs = 16)
        assertEquals(500f, mascot.x)
        assertEquals(FloconMascotMode.Backflip, mascot.mode)

        mascot.update(100, 1_000, 900f, 0f, 1_000f)
        assertEquals(FloconMascotMode.Sit, mascot.mode)
    }

    @Test
    fun goesUpInTheAirDuringABackflip() {
        val flipping = FloconMascotState(
            startX = 500f,
            config = FloconMascotConfig(
                stopDistance = 30f,
                wakeDistance = 100f,
                speedPerSecond = 100f,
                runDistance = 400f,
                runSpeedPerSecond = 300f,
                jumpHeight = 20f,
            ),
        )
        val platforms = listOf(FloconMascotPlatform(0f, 1_000f, y = 100f))
        flipping.update(16, 0, 500f, platforms)
        flipping.backflip()
        flipping.runOn(platforms, targetX = 500f, durationMs = 450, startMs = 16)
        assertTrue(flipping.y < 100f - 10f, "y=${flipping.y}")

        flipping.runOn(platforms, targetX = 500f, durationMs = 600, startMs = 466)
        assertEquals(100f, flipping.y)
    }

    @Test
    fun backflipCanNotStartInTheMiddleOfAJump() {
        val mascot = state(startX = 300f)
        var time = 0L
        while (mascot.mode != FloconMascotMode.Jump && time < 10_000) {
            mascot.update(frameMs, time, 50f, islands)
            time += frameMs
        }
        mascot.backflip()
        mascot.update(frameMs, time, 50f, islands)
        assertEquals(FloconMascotMode.Jump, mascot.mode)
    }

    @Test
    fun clickingDuringABackflipDoesNotInterruptIt() {
        val mascot = state()
        mascot.backflip()
        mascot.update(16, 0, 500f, 0f, 1_000f)
        mascot.pet()
        mascot.update(16, 16, 500f, 0f, 1_000f)
        assertEquals(FloconMascotMode.Backflip, mascot.mode)
    }
}
