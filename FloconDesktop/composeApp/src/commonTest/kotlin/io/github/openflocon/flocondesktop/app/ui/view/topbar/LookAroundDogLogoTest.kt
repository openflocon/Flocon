package io.github.openflocon.flocondesktop.app.ui.view.topbar

import kotlin.test.Test
import kotlin.test.assertEquals

class LookAroundDogLogoTest {

    @Test
    fun cardinalDirectionsMapToLottieProgress() {
        assertEquals(0f, lookAroundProgress(dx = -10f, dy = 0f), 0.001f) // left
        assertEquals(0.25f, lookAroundProgress(dx = 0f, dy = -10f), 0.001f) // top
        assertEquals(0.5f, lookAroundProgress(dx = 10f, dy = 0f), 0.001f) // right
        assertEquals(0.75f, lookAroundProgress(dx = 0f, dy = 10f), 0.001f) // bottom
    }

    @Test
    fun shortestDeltaCrossesTheLeftSeam() {
        assertEquals(0.1f, shortestDelta(from = 0.95f, to = 0.05f), 0.001f)
        assertEquals(-0.1f, shortestDelta(from = 0.05f, to = 0.95f), 0.001f)
        assertEquals(0.2f, shortestDelta(from = 0.3f, to = 0.5f), 0.001f)
    }
}
