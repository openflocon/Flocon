package io.github.openflocon.flocon.plugins.files

import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconFile
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.core.FloconFileSender
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.model.FloconFileInfo
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

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
            body = "{\"requestId\":\"42\",\"path\":\"/flocon/does/not/exist\",\"isConstantPath\":false}",
        )

        val (pluginName, method, body) = sender.messages.single()
        assertEquals(Protocol.FromDevice.Files.Plugin, pluginName)
        assertEquals(Protocol.FromDevice.Files.Method.ListFiles, method)
        assertEquals("{\"requestId\":\"42\",\"files\":[]}", body)
    }

    @Test
    fun `listFiles lists the content of a folder`() = runTest {
        val dir = kotlin.io.path.createTempDirectory("flocon-files").toFile()
        try {
            java.io.File(dir, "a.txt").writeText("abc")
            java.io.File(dir, "sub").mkdir()

            val files = FileDataSourceJvm().getFolderContent(dir.absolutePath, isConstantPath = false, withFoldersSize = true)

            assertEquals(setOf("a.txt", "sub"), files.map { it.name }.toSet())
            assertEquals(3L, files.single { it.name == "a.txt" }.size)
            assertEquals(true, files.single { it.name == "sub" }.isDirectory)
        } finally {
            dir.deleteRecursively()
        }
    }

    @Test
    fun `deleteFile removes files and folders`() {
        val dir = kotlin.io.path.createTempDirectory("flocon-files").toFile()
        try {
            val file = java.io.File(dir, "a.txt").apply { writeText("abc") }
            val dataSource = FileDataSourceJvm()

            dataSource.deleteFile(file.absolutePath)
            assertEquals(false, file.exists())

            java.io.File(dir, "b.txt").writeText("b")
            dataSource.deleteFolderContent(dataSource.getFile(dir.absolutePath, isConstantPath = false)!!)
            assertEquals(0, dir.listFiles()!!.size)
        } finally {
            dir.deleteRecursively()
        }
    }

    @Test
    fun `key is the plugin name sent by the desktop`() {
        assertEquals(Protocol.ToDevice.Files.Plugin, plugin.key)
    }
}
