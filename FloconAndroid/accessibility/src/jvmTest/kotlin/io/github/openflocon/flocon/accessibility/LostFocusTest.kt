package io.github.openflocon.flocon.accessibility

import io.github.openflocon.flocon.accessibility.model.AccessibilityEvent
import kotlin.test.Test
import kotlin.test.assertEquals

class LostFocusTest {

    private val focused = AccessibilityEvent(
        type = "VIEW_ACCESSIBILITY_FOCUSED",
        text = "Add to cart",
        className = "android.widget.Button",
        screen = "MainActivity",
        bounds = "[0,0][100,50]",
        extras = mapOf("role" to "Button", "spoken" to "Add to cart, Button", "packageName" to "old"),
    )

    private val cleared = AccessibilityEvent(
        type = "VIEW_ACCESSIBILITY_FOCUS_CLEARED",
        className = "android.view.View",
        screen = "MainActivity",
        extras = mapOf("packageName" to "com.example"),
    )

    @Test
    fun `a cleared focus is described by the element that had it`() {
        val result = cleared.describingLostFocus(focused)

        assertEquals("VIEW_ACCESSIBILITY_FOCUS_CLEARED", result.type)
        assertEquals("Add to cart", result.text)
        assertEquals("android.widget.Button", result.className)
        assertEquals("[0,0][100,50]", result.bounds)
        assertEquals("Button", result.extras["role"])
        assertEquals("Add to cart, Button", result.extras["spoken"])
        assertEquals("true", result.extras["lostFocus"])
    }

    @Test
    fun `the extras of the cleared event win over the ones of the old element`() {
        assertEquals("com.example", cleared.describingLostFocus(focused).extras["packageName"])
    }

    @Test
    fun `nothing is added when no element is known`() {
        assertEquals(cleared, cleared.describingLostFocus(null))
    }
}
