package io.github.openflocon.flocon.accessibility

import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.accessibility.model.AccessibilityEvent
import io.github.openflocon.flocon.accessibility.model.AccessibilityStateItem
import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.core.FloconMessageSender
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class FloconAccessibilityPluginTest {

    private class FakeFloconMessageSender : FloconMessageSender {
        val messages = mutableListOf<Triple<String, String, String>>()
        override fun send(plugin: String, method: String, body: String) {
            messages += Triple(plugin, method, body)
        }
        override fun sendPendingMessages() {}
    }

    private val sender = FakeFloconMessageSender()
    private val plugin = FloconAccessibilityPluginImpl(sender, FloconEncoder())

    @Test
    fun `log sends the event`() {
        plugin.log(
            AccessibilityEvent(
                type = "VIEW_FOCUSED",
                text = "Buy",
                contentDescription = "Buy button",
                className = "android.widget.Button",
                screen = "MainActivity",
                bounds = "[0,0][100,50]",
                extras = mapOf("packageName" to "com.example"),
            )
        )

        val (pluginName, method, body) = sender.messages.single()
        assertEquals(Protocol.FromDevice.Accessibility.Plugin, pluginName)
        assertEquals(Protocol.FromDevice.Accessibility.Method.AddEvents, method)
        assertContains(body, "\"type\":\"VIEW_FOCUSED\"")
        assertContains(body, "\"contentDescription\":\"Buy button\"")
        assertContains(body, "\"className\":\"android.widget.Button\"")
        assertContains(body, "\"bounds\":\"[0,0][100,50]\"")
        assertContains(body, "\"packageName\":\"com.example\"")
        assertContains(body, "\"createdAt\":")
    }

    @Test
    fun `announce logs an announcement`() {
        plugin.announce("Item added")

        val (_, method, body) = sender.messages.single()
        assertEquals(Protocol.FromDevice.Accessibility.Method.AddEvents, method)
        assertContains(body, "\"type\":\"ANNOUNCEMENT\"")
        assertContains(body, "\"text\":\"Item added\"")
    }

    @Test
    fun `log sends nothing for an empty list`() {
        plugin.log(emptyList())

        assertEquals(0, sender.messages.size)
    }

    @Test
    fun `state is sent again when connected`() {
        plugin.sendState(AccessibilityStateItem(supported = true, enabled = true, touchExploration = false, services = listOf("talkback")))
        runBlocking { plugin.onConnectedToServer() }

        assertEquals(2, sender.messages.size)
        sender.messages.forEach { (pluginName, method, body) ->
            assertEquals(Protocol.FromDevice.Accessibility.Plugin, pluginName)
            assertEquals(Protocol.FromDevice.Accessibility.Method.State, method)
            assertContains(body, "\"supported\":true")
            assertContains(body, "\"enabled\":true")
            assertContains(body, "\"touchExploration\":false")
            assertContains(body, "\"services\":[\"talkback\"]")
        }
    }

    @Test
    fun `an accessibility focus event sends the focused node`() {
        plugin.log(AccessibilityEvent(type = "VIEW_ACCESSIBILITY_FOCUSED", text = "Buy", bounds = "[0,0][100,50]"))

        val (pluginName, method, body) = sender.messages.last()
        assertEquals(Protocol.FromDevice.Accessibility.Plugin, pluginName)
        assertEquals(Protocol.FromDevice.Accessibility.Method.FocusedNode, method)
        assertContains(body, "\"found\":true")
        assertContains(body, "\"text\":\"Buy\"")
        assertContains(body, "\"bounds\":\"[0,0][100,50]\"")
    }

    @Test
    fun `clearing the accessibility focus sends an empty focused node`() {
        plugin.log(AccessibilityEvent(type = "VIEW_ACCESSIBILITY_FOCUS_CLEARED"))

        val (_, method, body) = sender.messages.last()
        assertEquals(Protocol.FromDevice.Accessibility.Method.FocusedNode, method)
        assertEquals("{}", body)
    }

    @Test
    fun `focus commands reply with the focused node`() {
        listOf(
            Protocol.ToDevice.Accessibility.Method.GetFocusedNode,
            Protocol.ToDevice.Accessibility.Method.FocusNext,
            Protocol.ToDevice.Accessibility.Method.FocusPrevious,
        ).forEach { command ->
            sender.messages.clear()
            runBlocking { plugin.onMessageReceived(command, "") }

            val (pluginName, method, _) = sender.messages.single()
            assertEquals(Protocol.FromDevice.Accessibility.Plugin, pluginName)
            assertEquals(Protocol.FromDevice.Accessibility.Method.FocusedNode, method)
        }
    }

    @Test
    fun `an unknown command is ignored`() {
        runBlocking { plugin.onMessageReceived("unknown", "") }

        assertEquals(0, sender.messages.size)
    }
}
