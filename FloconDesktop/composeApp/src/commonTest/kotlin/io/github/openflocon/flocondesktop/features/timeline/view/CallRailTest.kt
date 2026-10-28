package io.github.openflocon.flocondesktop.features.timeline.view

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CallRailTest {

    @Test
    fun `the bar goes from the response to the request, with a dot on each`() {
        assertEquals(
            CallRailSpan(top = 40f, bottom = 300f, topDot = true, bottomDot = true),
            callRailSpan(upperCenter = 40f, lowerCenter = 300f, viewportHeight = 600f),
        )
    }

    @Test
    fun `a request scrolled out below leaves the list at the bottom`() {
        assertEquals(
            CallRailSpan(top = 40f, bottom = 600f, topDot = true, bottomDot = false),
            callRailSpan(upperCenter = 40f, lowerCenter = null, viewportHeight = 600f),
        )
    }

    @Test
    fun `a response scrolled out above leaves the list at the top`() {
        assertEquals(
            CallRailSpan(top = 0f, bottom = 300f, topDot = false, bottomDot = true),
            callRailSpan(upperCenter = null, lowerCenter = 300f, viewportHeight = 600f),
        )
    }

    @Test
    fun `nothing is drawn when none of the two lines is displayed`() {
        assertNull(callRailSpan(upperCenter = null, lowerCenter = null, viewportHeight = 600f))
    }

    @Test
    fun `the center of an item of a list going down is measured from the top`() {
        assertEquals(60f, itemCenter(offset = 40, size = 40, viewportHeight = 600f, reversed = false))
    }

    @Test
    fun `the center of an item of an inverted list is measured from the bottom`() {
        // the first item of an inverted list is at the bottom of the viewport
        assertEquals(580f, itemCenter(offset = 0, size = 40, viewportHeight = 600f, reversed = true))
        assertEquals(540f, itemCenter(offset = 40, size = 40, viewportHeight = 600f, reversed = true))
    }
}
