package io.github.openflocon.mcp.documents

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.common.Success
import io.github.openflocon.domain.device.usecase.GetFloconDocumentPathUseCase
import io.github.openflocon.domain.models.settings.NetworkSettings
import io.github.openflocon.domain.models.settings.ThemeSetting
import io.github.openflocon.domain.settings.repository.AdbForwardStatus
import io.github.openflocon.domain.settings.repository.SettingsRepository
import io.github.openflocon.mcp.McpTestServer
import io.github.openflocon.mcp.json
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.jsonPrimitive
import java.io.File
import java.nio.file.Files
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FloconFolderMcpToolProviderTest {

    private val tempDir = Files.createTempDirectory("flocon-folder-test").toFile()
    private val root = File(tempDir, "Flocon")

    private val opened = mutableListOf<File>()
    private var openResult: Either<Throwable, Unit> = Success(Unit)

    private val server = McpTestServer(
        toolProviders = listOf(
            FloconFolderMcpToolProvider(
                getFloconDocumentPathUseCase = GetFloconDocumentPathUseCase(FakeSettingsRepository(root.absolutePath)),
                folderOpener = FolderOpener {
                    opened += it
                    openResult
                },
            ),
        ),
    )

    @BeforeTest
    fun setUp() = runBlocking { server.start() }

    @AfterTest
    fun tearDown() = runBlocking {
        server.stop()
        tempDir.deleteRecursively()
        Unit
    }

    @Test
    fun `open_flocon_folder is listed, not read only and idempotent`() = runBlocking {
        val tool = server.listTools().single()

        assertEquals("open_flocon_folder", tool.name)
        assertEquals(false, tool.annotations?.readOnlyHint)
        assertEquals(false, tool.annotations?.destructiveHint)
        assertEquals(true, tool.annotations?.idempotentHint)
    }

    @Test
    fun `open_flocon_folder creates the folder, opens it and returns its path`() = runBlocking {
        assertTrue(!root.exists())

        val result = server.callTool("open_flocon_folder").json()

        assertTrue(root.isDirectory)
        assertEquals(listOf(root), opened)
        assertEquals(root.absolutePath, result["path"]!!.jsonPrimitive.content)
    }

    @Test
    fun `open_flocon_folder opens a sub folder`() = runBlocking {
        val result = server.callTool("open_flocon_folder", mapOf("subfolder" to "Screenshot")).json()

        val expected = File(root, "Screenshot")
        assertTrue(expected.isDirectory)
        assertEquals(listOf(expected), opened)
        assertEquals(expected.absolutePath, result["path"]!!.jsonPrimitive.content)
    }

    @Test
    fun `open_flocon_folder rejects paths leaving the documents folder`() = runBlocking {
        for (subfolder in listOf("..", "../secret", "Screenshot/../..", "/etc")) {
            val result = server.callTool("open_flocon_folder", mapOf("subfolder" to subfolder))
            assertEquals(true, result.isError, subfolder)
        }
        assertTrue(opened.isEmpty())
    }

    @Test
    fun `open_flocon_folder returns an error when the folder cannot be opened`() = runBlocking {
        openResult = Failure(IllegalStateException("no file manager"))

        assertEquals(true, server.callTool("open_flocon_folder").isError)
    }

    private class FakeSettingsRepository(private val path: String?) : SettingsRepository {
        override var networkSettings: NetworkSettings = NetworkSettings(
            pinnedDetails = false,
            displayOldSessions = false,
            autoScroll = false,
            invertList = false,
        )
        override val networkSettingsFlow: Flow<NetworkSettings> = emptyFlow()
        override fun getAdbPath(): String? = null
        override suspend fun setAdbPath(path: String) {}
        override suspend fun setFontSizeMultiplier(value: Float) {}
        override suspend fun setTheme(value: ThemeSetting) {}
        override fun getFloconDocumentsPath(): String? = path
        override suspend fun setFloconDocumentsPath(path: String) {}
        override val floconDocumentsPath: Flow<String?> = emptyFlow()
        override fun getDismissedDesktopVersion(): String? = null
        override suspend fun setDismissedDesktopVersion(version: String) {}
        override val dismissedClientVersionFlow: Flow<String?> = emptyFlow()
        override fun getDismissedClientVersion(): String? = null
        override suspend fun setDismissedClientVersion(version: String) {}
        override fun getLastSeenReleaseNotesVersion(): String? = null
        override suspend fun setLastSeenReleaseNotesVersion(version: String) {}
        override val adbPath: Flow<String?> = emptyFlow()
        override val fontSizeMultiplier: StateFlow<Float> = MutableStateFlow(1f)
        override val theme: StateFlow<ThemeSetting> = MutableStateFlow(ThemeSetting.DEFAULT)
        override val adbForwardStatus: StateFlow<AdbForwardStatus> = MutableStateFlow(AdbForwardStatus.UNKNOWN)
        override fun setAdbForwardStatus(status: AdbForwardStatus) {}
    }
}
