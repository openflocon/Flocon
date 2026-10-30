package io.github.openflocon.flocon.accessibility

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

class NodeDetailsTest {

    @Test
    fun `a clickable button is read with its label, its role and what a double tap does`() {
        val details = NodeDetails(label = "Add to cart", role = "Button", clickable = true)

        assertEquals("Add to cart, Button, double tap to activate", details.spokenText())
    }

    @Test
    fun `the click label replaces activate`() {
        val details = NodeDetails(label = "Photo", role = "Image", clickable = true, clickLabel = "open the gallery")

        assertEquals("Photo, Image, double tap to open the gallery", details.spokenText())
    }

    @Test
    fun `the state and the hint come after the role`() {
        val details = NodeDetails(label = "Wi-Fi", role = "Switch", state = "on", hint = "Turns the network on", clickable = true)

        assertEquals("Wi-Fi, Switch, on, Turns the network on, double tap to activate", details.spokenText())
    }

    @Test
    fun `a disabled element cannot be activated`() {
        val details = NodeDetails(label = "Buy", role = "Button", clickable = true, enabled = false)

        assertEquals("Buy, Button, disabled", details.spokenText())
    }

    @Test
    fun `a heading and a selected element say so`() {
        assertEquals("Settings, heading", NodeDetails(label = "Settings", heading = true).spokenText())
        assertEquals("Home, Tab, selected", NodeDetails(label = "Home", role = "Tab", selected = true).spokenText())
    }

    @Test
    fun `nothing to say gives no spoken text`() {
        assertNull(NodeDetails().spokenText())
        assertNull(NodeDetails(label = "  ").spokenText())
    }

    @Test
    fun `extras only hold what tells something`() {
        val extras = NodeDetails(label = "Buy", role = "Button", clickable = true).toExtras()

        assertEquals(
            mapOf(
                "enabled" to "true",
                "role" to "Button",
                "clickable" to "true",
                "spoken" to "Buy, Button, double tap to activate",
            ),
            extras,
        )
        assertFalse("heading" in extras)
        assertFalse("selected" in extras)
    }

    @Test
    fun `extras hold the state, the hint and the custom actions`() {
        val extras = NodeDetails(
            label = "Mail",
            state = "unread",
            hint = "Swipe to archive",
            clickLabel = "open",
            clickable = true,
            heading = true,
            selected = true,
            customActions = listOf("Archive", "Delete"),
        ).toExtras()

        assertEquals("unread", extras["stateDescription"])
        assertEquals("Swipe to archive", extras["hint"])
        assertEquals("open", extras["clickLabel"])
        assertEquals("true", extras["heading"])
        assertEquals("true", extras["selected"])
        assertEquals("Archive | Delete", extras["customActions"])
    }

    @Test
    fun `widget classes are read like a screen reader`() {
        assertEquals("Button", roleFromClassName("android.widget.Button"))
        assertEquals("Check box", roleFromClassName("android.widget.CheckBox"))
        assertEquals("Edit box", roleFromClassName("android.widget.EditText"))
        assertNull(roleFromClassName("android.widget.TextView"))
        assertNull(roleFromClassName(null))
    }

    @Test
    fun `a checkable element says on or off for a switch, checked otherwise`() {
        assertEquals("on", checkedState("Switch", true))
        assertEquals("off", checkedState("Switch", false))
        assertEquals("checked", checkedState("Check box", true))
        assertEquals("not checked", checkedState(null, false))
    }
}
