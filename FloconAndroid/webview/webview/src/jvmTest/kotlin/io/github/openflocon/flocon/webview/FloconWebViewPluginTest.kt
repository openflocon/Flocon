package io.github.openflocon.flocon.webview

import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.core.FloconMessageSender
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class FloconWebViewPluginTest {

    private class FakeFloconMessageSender : FloconMessageSender {
        val messages = mutableListOf<Triple<String, String, String>>()
        override fun send(plugin: String, method: String, body: String) {
            messages += Triple(plugin, method, body)
        }
        override fun sendPendingMessages() {}
    }

    private val sender = FakeFloconMessageSender()
    private val plugin = FloconWebViewPluginImpl(sender, FloconEncoder())

    @Test
    fun `logConsole sends the level, message and source`() {
        plugin.logConsole(
            webViewId = "checkout",
            level = WebViewConsoleLevel.Error,
            message = "x is undefined",
            url = "https://example.com",
            sourceId = "app.js",
            lineNumber = 42,
        )

        val (pluginName, method, body) = sender.messages.single()
        assertEquals(Protocol.FromDevice.WebView.Plugin, pluginName)
        assertEquals(Protocol.FromDevice.WebView.Method.AddConsoleLogs, method)
        assertContains(body, "\"web_view_id\":\"checkout\"")
        assertContains(body, "\"level\":\"error\"")
        assertContains(body, "\"message\":\"x is undefined\"")
        assertContains(body, "\"source_id\":\"app.js\"")
        assertContains(body, "\"line_number\":42")
    }

    @Test
    fun `logPageEvent sends the type and the status code`() {
        plugin.logPageEvent(
            webViewId = "checkout",
            type = WebViewPageEventType.HttpError,
            url = "https://example.com/api",
            statusCode = 500,
        )

        val (_, method, body) = sender.messages.single()
        assertEquals(Protocol.FromDevice.WebView.Method.AddPageEvents, method)
        assertContains(body, "\"type\":\"http_error\"")
        assertContains(body, "\"status_code\":500")
    }

    @Test
    fun `logBridgeCall sends the interface, method and arguments`() {
        plugin.logBridgeCall(
            webViewId = "checkout",
            interfaceName = "Android",
            method = "share",
            arguments = "hello",
        )

        val (_, method, body) = sender.messages.single()
        assertEquals(Protocol.FromDevice.WebView.Method.AddBridgeCalls, method)
        assertContains(body, "\"interface_name\":\"Android\"")
        assertContains(body, "\"method\":\"share\"")
        assertContains(body, "\"arguments\":\"hello\"")
    }

    @Test
    fun `each event gets its own id`() {
        plugin.logConsole("a", WebViewConsoleLevel.Log, "one")
        plugin.logConsole("a", WebViewConsoleLevel.Log, "two")

        val ids = sender.messages.map { Regex("\"id\":\"([^\"]+)\"").find(it.third)!!.groupValues[1] }
        assertEquals(2, ids.toSet().size)
        assertFalse(ids.any { it.isEmpty() })
    }
}
