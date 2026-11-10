package io.github.openflocon.flocondesktop.features.timeline.view

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CallRailTest {

    @Test
    fun `the bar goes from the first to the last linked event, with a dot on each`() {
        assertEquals(
            CallRailSpan(top = 40f, bottom = 300f, dots = listOf(40f, 120f, 300f)),
            callRailSpan(centers = listOf(40f, 120f, 300f), leavesTop = false, leavesBottom = false, viewportHeight = 600f),
        )
    }

    @Test
    fun `an event scrolled out below leaves the list at the bottom`() {
        assertEquals(
            CallRailSpan(top = 40f, bottom = 600f, dots = listOf(40f)),
            callRailSpan(centers = listOf(40f), leavesTop = false, leavesBottom = true, viewportHeight = 600f),
        )
    }

    @Test
    fun `an event scrolled out above leaves the list at the top`() {
        assertEquals(
            CallRailSpan(top = 0f, bottom = 300f, dots = listOf(300f)),
            callRailSpan(centers = listOf(300f), leavesTop = true, leavesBottom = false, viewportHeight = 600f),
        )
    }

    @Test
    fun `the bar crosses the list when the linked events are out of both sides`() {
        assertEquals(
            CallRailSpan(top = 0f, bottom = 600f, dots = emptyList()),
            callRailSpan(centers = emptyList(), leavesTop = true, leavesBottom = true, viewportHeight = 600f),
        )
    }

    @Test
    fun `nothing is drawn when no linked event is displayed and the bar does not cross the list`() {
        assertNull(callRailSpan(centers = emptyList(), leavesTop = true, leavesBottom = false, viewportHeight = 600f))
        assertNull(callRailSpan(centers = emptyList(), leavesTop = false, leavesBottom = false, viewportHeight = 600f))
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

    @Test
    fun `the padding before the content moves the items, it is not part of their offset`() {
        // an inverted list with a padding of 20 at the bottom: the first item is above it
        assertEquals(560f, itemCenter(offset = 0, size = 40, viewportHeight = 600f, reversed = true, startPadding = 20))
    }
}
