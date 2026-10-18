package io.github.openflocon.flocon.plugins.files

import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.core.FloconFileSender
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconFile
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.KSerializer
import kotlin.test.Test
import kotlin.test.assertTrue

class FloconFilesPluginTest {

    private class FakeFloconFileSender : FloconFileSender {
        override fun sendFile(file: FloconFile) {}
        override fun deleteFile(path: String) {}
        override fun deleteFiles(paths: List<String>) {}
    }

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
    fun `onMessageReceived does not throw`() = runTest {
        val floconFileSender = FakeFloconFileSender()
        val sender = FakeFloconMessageSender()
        val encoder = FakeFloconEncoder()
        val context = FloconContext()
        val plugin = FloconFilesPluginImpl(context, floconFileSender, sender, encoder)

        plugin.onMessageReceived("dummy_method", "dummy_body")
        assertTrue(true)
    }
}
