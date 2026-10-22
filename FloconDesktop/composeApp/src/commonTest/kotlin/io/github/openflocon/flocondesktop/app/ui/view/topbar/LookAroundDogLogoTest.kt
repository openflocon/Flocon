package io.github.openflocon.flocondesktop.app.ui.view.topbar

import kotlin.test.Test
import kotlin.test.assertEquals

class LookAroundDogLogoTest {

    @Test
    fun lookFrameMapsTheGrid() {
        assertEquals(0, lookFrame(x = -1f, y = -1f)) // up left
        assertEquals(14, lookFrame(x = 1f, y = -1f)) // up right
        assertEquals(112, lookFrame(x = 0f, y = 0f)) // straight
        assertEquals(210, lookFrame(x = -1f, y = 1f)) // down left
        assertEquals(224, lookFrame(x = 1f, y = 1f)) // down right
    }

    @Test
    fun lookDirectionIsClamped() {
        val far = lookDirection(dx = 10_000f, dy = -10_000f)
        assertEquals(1f, far.x, 0.001f)
        assertEquals(-1f, far.y, 0.001f)
    }

    @Test
    fun lookProgressStaysInsideTheFrame() {
        assertEquals(112.5f / 225f, lookProgress(x = 0f, y = 0f), 0.0001f)
        assertEquals(224.5f / 225f, lookProgress(x = 1f, y = 1f), 0.0001f)
    }
}
