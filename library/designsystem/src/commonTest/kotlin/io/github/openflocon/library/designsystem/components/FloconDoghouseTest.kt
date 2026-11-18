package io.github.openflocon.library.designsystem.components

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FloconDoghouseTest {

    @Test
    fun sitsNextToTheHouseOnceOutside() {
        assertEquals(40 + 4, doghouseMascotLeft(walk = 1f))
    }

    @Test
    fun isBehindTheDoorLineOnceInside() {
        // the sprite ends on the door line, so it is entirely clipped away
        assertEquals(20, doghouseMascotLeft(walk = 0f) + 34)
    }

    @Test
    fun walksTowardTheHouseAsTheWalkRewinds() {
        val lefts = listOf(1f, 0.75f, 0.5f, 0.25f, 0f).map(::doghouseMascotLeft)
        assertTrue(lefts.zipWithNext().all { (a, b) -> a > b })
    }

    @Test
    fun theMascotFoldsThePanel() {
        assertTrue(doghouseTapToggles(expanded = true, xPx = 60f))
        assertFalse(doghouseTapToggles(expanded = true, xPx = 20f))
    }

    @Test
    fun cutsTheHouseOnItsLeftWhenTheWidthIsTooShort() {
        assertEquals(-6, doghouseShift(unit = 2f, widthPx = 68f))
        assertEquals(0, doghouseShift(unit = 2f, widthPx = 200f))
    }

    @Test
    fun theHouseUnfoldsThePanel() {
        assertTrue(doghouseTapToggles(expanded = false, xPx = 20f))
        assertFalse(doghouseTapToggles(expanded = false, xPx = 60f))
    }
}
