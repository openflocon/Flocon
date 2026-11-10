package io.github.openflocon.flocondesktop.features.accessibility.mapper

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class TypeLabelTest {

    @Test
    fun `the common event types have a readable name`() {
        listOf(
            "ANNOUNCEMENT",
            "CLICK",
            "VIEW_ACCESSIBILITY_FOCUSED",
            "VIEW_ACCESSIBILITY_FOCUS_CLEARED",
            "WINDOW_STATE_CHANGED",
        ).forEach { assertNotNull(accessibilityTypeLabelRes(it), it) }
    }

    @Test
    fun `an unknown type has no name`() {
        assertNull(accessibilityTypeLabelRes("VIEW_HOVER_ENTER"))
    }

    @Test
    fun `the fallback is the type in words`() {
        assertEquals("View hover enter", fallbackTypeLabel("VIEW_HOVER_ENTER"))
        assertEquals("Custom", fallbackTypeLabel("CUSTOM"))
    }

    @Test
    fun `the fallback never gives an empty name`() {
        assertEquals("_", fallbackTypeLabel("_"))
    }
}
