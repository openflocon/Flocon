package io.github.openflocon.flocon.deeplinks

import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.deeplinks.model.DeeplinkModel
import io.github.openflocon.flocon.deeplinks.model.DeeplinkVariable
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.KSerializer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FloconDeeplinksPluginTest {

    private class FakeFloconMessageSender : FloconMessageSender {
        var lastSentBody: String? = null
        override fun send(plugin: String, method: String, body: String) {
            lastSentBody = body
        }
        override fun sendPendingMessages() {}
    }

    private class FakeFloconEncoder : FloconEncoder {
        override fun <T> encode(serializer: KSerializer<T>, body: T): String = "encoded_${body.toString()}"
        override fun <T> decode(serializer: KSerializer<T>, body: String): T = throw UnsupportedOperationException()
    }

    @Test
    fun `registerDeeplinks sends correct message`() = runTest {
        val sender = FakeFloconMessageSender()
        val encoder = FakeFloconEncoder()
        val deeplinks = listOf(
            DeeplinkModel(id = "1", link = "https://example.com")
        )
        val variables = listOf(
            DeeplinkVariable(name = "var1")
        )
        val plugin = FloconDeeplinksPluginImpl(deeplinks, variables, sender, encoder)

        plugin.registerDeeplinks(deeplinks, variables)

        assertEquals("Deeplink", sender.lastSentBody?.contains("Deeplink") ?: false)
        assertTrue(sender.lastSentBody!!.contains("example.com"))
    }
}
