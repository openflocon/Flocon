package io.github.openflocon.flocon.accessibility

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AccessibilityFocusTest {

    @Test
    fun `next starts at the first element when nothing has the focus`() {
        assertEquals(0, targetFocusIndex(AccessibilityFocusRequest.Next, currentIndex = -1, count = 3))
    }

    @Test
    fun `previous starts at the last element when nothing has the focus`() {
        assertEquals(2, targetFocusIndex(AccessibilityFocusRequest.Previous, currentIndex = -1, count = 3))
    }

    @Test
    fun `next and previous move by one`() {
        assertEquals(2, targetFocusIndex(AccessibilityFocusRequest.Next, currentIndex = 1, count = 3))
        assertEquals(0, targetFocusIndex(AccessibilityFocusRequest.Previous, currentIndex = 1, count = 3))
    }

    @Test
    fun `the order does not wrap`() {
        assertNull(targetFocusIndex(AccessibilityFocusRequest.Next, currentIndex = 2, count = 3))
        assertNull(targetFocusIndex(AccessibilityFocusRequest.Previous, currentIndex = 0, count = 3))
    }

    @Test
    fun `current is the focused element, or nothing`() {
        assertEquals(1, targetFocusIndex(AccessibilityFocusRequest.Current, currentIndex = 1, count = 3))
        assertNull(targetFocusIndex(AccessibilityFocusRequest.Current, currentIndex = -1, count = 3))
    }

    @Test
    fun `activate stays on the focused element, or nothing`() {
        assertEquals(1, targetFocusIndex(AccessibilityFocusRequest.Activate, currentIndex = 1, count = 3))
        assertNull(targetFocusIndex(AccessibilityFocusRequest.Activate, currentIndex = -1, count = 3))
    }

    @Test
    fun `an empty screen has nothing to focus`() {
        AccessibilityFocusRequest.entries.forEach {
            assertNull(targetFocusIndex(it, currentIndex = -1, count = 0))
        }
    }
}
