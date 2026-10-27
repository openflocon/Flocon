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

    private val keys = listOf("a", "response", "b", "c", "request", "d")

    @Test
    fun `the items between the two lines of a call lose their rail`() {
        assertEquals(setOf("b", "c"), keysBetween(keys, upperKey = "response", lowerKey = "request"))
    }

    @Test
    fun `two adjacent lines have nothing between them`() {
        assertEquals(emptySet(), keysBetween(listOf("response", "request"), upperKey = "response", lowerKey = "request"))
    }

    @Test
    fun `an end scrolled out of the list is the edge of the list`() {
        assertEquals(setOf("b", "c", "request", "d"), keysBetween(keys, upperKey = "response", lowerKey = "gone"))
        assertEquals(setOf("a", "response", "b", "c"), keysBetween(keys, upperKey = "gone", lowerKey = "request"))
    }

    @Test
    fun `nothing loses its rail without a response or without a displayed line`() {
        assertEquals(emptySet(), keysBetween(keys, upperKey = null, lowerKey = "request"))
        assertEquals(emptySet(), keysBetween(keys, upperKey = "gone", lowerKey = "gone-too"))
    }
}
