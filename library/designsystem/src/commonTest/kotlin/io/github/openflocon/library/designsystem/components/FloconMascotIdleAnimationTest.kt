package io.github.openflocon.library.designsystem.components

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FloconMascotIdleAnimationTest {

    @Test
    fun startsOnItsFirstPicture() {
        FloconMascotIdleAnimation.entries.forEach {
            assertEquals(0, it.frameAt(0), it.name)
        }
    }

    @Test
    fun stopsOnItsLastPicture() {
        assertEquals(4, FloconMascotIdleAnimation.Scratch.frameAt(FloconMascotIdleAnimation.Scratch.durationMs + 10))
        assertEquals(5, FloconMascotIdleAnimation.Stretch.frameAt(FloconMascotIdleAnimation.Stretch.durationMs + 10))
    }

    @Test
    fun followsItsSteps() {
        // 700 ms on the first picture, then 350 ms on the second
        assertEquals(0, FloconMascotIdleAnimation.Scratch.frameAt(699))
        assertEquals(1, FloconMascotIdleAnimation.Scratch.frameAt(700))
        assertEquals(2, FloconMascotIdleAnimation.Scratch.frameAt(1_050))
    }

    @Test
    fun theStretchOpensItsMouthOnTheGround() {
        val stretch = FloconMascotIdleAnimation.Stretch
        // 600 + 450 + 450 + 350 ms later, the mouth opens for a second
        assertEquals(4, stretch.frameAt(1_900))
        assertEquals(4, stretch.frameAt(2_900))
        assertEquals(3, stretch.frameAt(3_100))
    }

    @Test
    fun lastsAFewSeconds() {
        FloconMascotIdleAnimation.entries.forEach {
            assertTrue(it.durationMs in 2_000..6_000, "${it.name}=${it.durationMs}")
        }
    }
}
