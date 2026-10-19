package io.github.openflocon.flocon.plugins.files

import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconFile
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.core.FloconFileSender
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.dsl.FloconMarker
import io.github.openflocon.flocon.model.FloconFileInfo
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(FloconMarker::class)
class FloconFilesPluginTest {

    private class FakeFloconFileSender : FloconFileSender {
        override fun send(file: FloconFile, infos: FloconFileInfo) {}
    }

    private class FakeFloconMessageSender : FloconMessageSender {
        val messages = mutableListOf<Triple<String, String, String>>()
        override fun send(plugin: String, method: String, body: String) {
            messages += Triple(plugin, method, body)
        }
        override fun sendPendingMessages() {}
    }

    private val sender = FakeFloconMessageSender()
    private val plugin = FloconFilesPluginImpl(
        context = FloconContext(appName = "app", packageName = "app.package"),
        floconFileSender = FakeFloconFileSender(),
        sender = sender,
        encoder = FloconEncoder(),
    )

    @Test
    fun `listFiles answers with the request id`() = runTest {
        plugin.onMessageReceived(
            method = Protocol.ToDevice.Files.Method.ListFiles,
            body = "{\"requestId\":\"42\",\"path\":\"/\",\"isConstantPath\":false}",
        )

        val (pluginName, method, body) = sender.messages.single()
        assertEquals(Protocol.FromDevice.Files.Plugin, pluginName)
        assertEquals(Protocol.FromDevice.Files.Method.ListFiles, method)
        assertEquals("{\"requestId\":\"42\",\"files\":[]}", body)
    }

    @Test
    fun `key is the plugin name sent by the desktop`() {
        assertEquals(Protocol.ToDevice.Files.Plugin, plugin.key)
    }
}
