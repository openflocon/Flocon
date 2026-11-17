package io.github.openflocon.flocon.plugins.files

import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconFile
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.core.FloconFileSender
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.model.FloconFileInfo
import io.github.openflocon.flocon.plugins.files.model.fromdevice.FileRootDataModel
import kotlinx.coroutines.test.runTest
import java.io.File
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FloconFilesPluginTest {

    private class FakeFloconFileSender : FloconFileSender {
        val sent = mutableListOf<FloconFileInfo>()
        override fun send(file: FloconFile, infos: FloconFileInfo) {
            sent += infos
        }
    }

    private class FakeFloconMessageSender : FloconMessageSender {
        val messages = mutableListOf<Triple<String, String, String>>()
        override fun send(plugin: String, method: String, body: String) {
            messages += Triple(plugin, method, body)
        }
        override fun sendPendingMessages() {}
    }

    // the root folder "files", and a folder outside of it
    private val root = kotlin.io.path.createTempDirectory("flocon-files").toFile().canonicalFile
    private val outside = kotlin.io.path.createTempDirectory("flocon-outside").toFile().canonicalFile

    private val sender = FakeFloconMessageSender()
    private val fileSender = FakeFloconFileSender()
    private val plugin = FloconFilesPluginImpl(
        context = FloconContext(appName = "app", packageName = "app.package"),
        floconFileSender = fileSender,
        sender = sender,
        encoder = FloconEncoder(),
        fileDataSource = JavaIoFileDataSource(
            defaultRoots = { listOf(FileRootDataModel(id = "files", name = "Files", path = root.absolutePath)) }
        ),
    )

    @AfterTest
    fun tearDown() {
        root.deleteRecursively()
        outside.deleteRecursively()
    }

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
    fun `listFiles lists the content of a root folder`() = runTest {
        File(root, "a.txt").writeText("abc")
        File(root, "sub").mkdir()

        val files = JavaIoFileDataSource(defaultRoots = { emptyList() })
            .getFolderContent(root.absolutePath, withFoldersSize = true)

        assertEquals(setOf("a.txt", "sub"), files.map { it.name }.toSet())
        assertEquals(3L, files.single { it.name == "a.txt" }.size)
        assertEquals(true, files.single { it.name == "sub" }.isDirectory)

        plugin.onMessageReceived(
            method = Protocol.ToDevice.Files.Method.ListFiles,
            body = "{\"requestId\":\"1\",\"path\":\"files\",\"isConstantPath\":true}",
        )
        assertTrue(sender.messages.single().third.contains("a.txt"))
    }

    @Test
    fun `getRoots sends the default and the registered folders`() = runTest {
        plugin.register(displayName = "Outside", absolutePath = outside.absolutePath)

        plugin.onMessageReceived(
            method = Protocol.ToDevice.Files.Method.GetRoots,
            body = "{\"requestId\":\"7\"}",
        )

        val (pluginName, method, body) = sender.messages.single()
        assertEquals(Protocol.FromDevice.Files.Plugin, pluginName)
        assertEquals(Protocol.FromDevice.Files.Method.GetRoots, method)
        assertEquals(
            "{\"requestId\":\"7\",\"roots\":[" +
                "{\"id\":\"files\",\"name\":\"Files\",\"path\":\"${root.absolutePath}\"}," +
                "{\"id\":\"${outside.absolutePath}\",\"name\":\"Outside\",\"path\":\"${outside.absolutePath}\"}" +
                "]}",
            body,
        )
    }

    @Test
    fun `getRoots skips a registered folder that does not exist`() = runTest {
        plugin.register(displayName = "Missing", absolutePath = "${outside.absolutePath}/missing")

        plugin.onMessageReceived(
            method = Protocol.ToDevice.Files.Method.GetRoots,
            body = "{\"requestId\":\"7\"}",
        )

        assertFalse(sender.messages.single().third.contains("Missing"))
    }

    @Test
    fun `deleteFile removes files and folders inside a root, never the root`() = runTest {
        val file = File(root, "a.txt").apply { writeText("abc") }
        val folder = File(root, "sub").apply { mkdir() }
        File(folder, "b.txt").writeText("b")

        plugin.onMessageReceived(
            method = Protocol.ToDevice.Files.Method.DeleteFiles,
            body = "{\"requestId\":\"1\",\"parentPath\":\"files\",\"filePaths\":[\"${file.absolutePath}\",\"${folder.absolutePath}\",\"${root.absolutePath}\"],\"isConstantParentPath\":true}",
        )

        assertFalse(file.exists())
        assertFalse(folder.exists())
        assertTrue(root.exists())
    }

    @Test
    fun `nothing outside the roots is deleted or sent`() = runTest {
        val secret = File(outside, "secret.txt").apply { writeText("secret") }

        plugin.onMessageReceived(
            method = Protocol.ToDevice.Files.Method.DeleteFile,
            body = "{\"requestId\":\"1\",\"parentPath\":\"files\",\"filePath\":\"${secret.absolutePath}\",\"isConstantParentPath\":true}",
        )
        plugin.onMessageReceived(
            method = Protocol.ToDevice.Files.Method.DeleteFile,
            body = "{\"requestId\":\"2\",\"parentPath\":\"files\",\"filePath\":\"${root.absolutePath}/../${outside.name}/secret.txt\",\"isConstantParentPath\":true}",
        )
        plugin.onMessageReceived(
            method = Protocol.ToDevice.Files.Method.GetFile,
            body = "{\"requestId\":\"3\",\"path\":\"${secret.absolutePath}\"}",
        )

        assertTrue(secret.exists())
        assertTrue(fileSender.sent.isEmpty())
    }

    @Test
    fun `deleteFolderContent empties a root folder`() = runTest {
        File(root, "a.txt").writeText("a")
        File(root, "sub").mkdir()

        plugin.onMessageReceived(
            method = Protocol.ToDevice.Files.Method.DeleteFolderContent,
            body = "{\"requestId\":\"1\",\"path\":\"files\",\"isConstantPath\":true}",
        )

        assertTrue(root.exists())
        assertEquals(0, root.listFiles()!!.size)
    }

    @Test
    fun `key is the plugin name sent by the desktop`() {
        assertEquals(Protocol.ToDevice.Files.Plugin, plugin.key)
    }
}
