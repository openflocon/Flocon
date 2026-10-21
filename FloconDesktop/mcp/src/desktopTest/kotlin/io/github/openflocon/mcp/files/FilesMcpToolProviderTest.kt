package io.github.openflocon.mcp.files

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.common.Success
import io.github.openflocon.domain.device.models.DeviceDomainModel
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.files.models.FileDomainModel
import io.github.openflocon.domain.files.models.FilePathDomainModel
import io.github.openflocon.domain.files.models.FileRootDomainModel
import io.github.openflocon.domain.files.repository.FilesRepository
import io.github.openflocon.domain.messages.models.FloconReceivedFileDomainModel
import io.github.openflocon.mcp.FakeDevicesRepository
import io.github.openflocon.mcp.McpTestServer
import io.github.openflocon.mcp.app
import io.github.openflocon.mcp.json
import io.github.openflocon.mcp.targetResolver
import io.github.openflocon.mcp.text
import io.modelcontextprotocol.kotlin.sdk.types.ImageContent
import io.modelcontextprotocol.kotlin.sdk.types.TextContent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.File
import java.nio.file.Files
import java.util.Base64
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Instant

class FilesMcpToolProviderTest {

    private val pixel = DeviceDomainModel(deviceId = "pixel", deviceName = "Pixel 9", platform = "android")
    private val devicesRepository = FakeDevicesRepository(
        devicesWithApps = mapOf(pixel to listOf(app("io.flocon.sample"))),
        currentDeviceIdValue = "pixel",
        selectedApps = mapOf("pixel" to "io.flocon.sample"),
    )
    private val downloadDir: File = Files.createTempDirectory("flocon-mcp-files").toFile()
    private val filesRepository = FakeFilesRepository(
        deviceRoots = listOf(
            FileRootDomainModel(id = "files", name = "Files", path = "/data/files"),
            FileRootDomainModel(id = "caches", name = "Caches", path = "/data/cache"),
            FileRootDomainModel(id = "/data/files/exports", name = "Exports", path = "/data/files/exports"),
        ),
        deviceContent = mapOf(
            FilePathDomainModel.Root("files") to listOf(
                file("/data/files/notes.txt"),
                file("/data/files/images", isDirectory = true),
                file("/data/files/avatar.png"),
            ),
            FilePathDomainModel.Real("/data/files/images") to listOf(file("/data/files/images/cat.png")),
            FilePathDomainModel.Root("/data/files/exports") to listOf(file("/data/files/exports/report.csv")),
        ),
        deviceFiles = mapOf(
            "/data/files/notes.txt" to "hello flocon".toByteArray(),
            "/data/files/big.json" to "x".repeat(500).toByteArray(),
            "/data/files/avatar.png" to byteArrayOf(0x89.toByte(), 'P'.code.toByte(), 'N'.code.toByte(), 'G'.code.toByte(), 0),
            "/data/files/app.db" to "SQLite format 3\u0000\u0000\u0001".toByteArray(),
            // OkHttp cache entry: no extension
            "/data/cache/a1b2.1" to "RIFF\u0084\u0026\u0000\u0000WEBPVP8 ".toByteArray(Charsets.ISO_8859_1),
        ),
        downloadDir = downloadDir,
    )

    private val server = McpTestServer(
        toolProviders = listOf(
            FilesMcpToolProvider(
                filesRepository = filesRepository,
                targetResolver = targetResolver(devicesRepository),
            ),
        ),
    )

    @BeforeTest
    fun setUp() = runBlocking { server.start() }

    @AfterTest
    fun tearDown() = runBlocking {
        server.stop()
        downloadDir.deleteRecursively()
        Unit
    }

    @Test
    fun `all the tools are read only`() = runBlocking {
        val tools = server.listTools()

        assertEquals(setOf("list_file_roots", "list_files", "read_file"), tools.map { it.name }.toSet())
        assertTrue(tools.all { it.annotations?.readOnlyHint == true })
    }

    @Test
    fun `list_file_roots returns the roots announced by the app`() = runBlocking {
        val result = server.callTool("list_file_roots").json()

        assertEquals(false, result["fromCache"]!!.jsonPrimitive.boolean)
        assertEquals(
            listOf("files", "caches", "/data/files/exports"),
            result["roots"]!!.jsonArray.map { it.jsonObject["id"]!!.jsonPrimitive.content },
        )
        assertEquals("/data/cache", result["roots"]!!.jsonArray[1].jsonObject["path"]!!.jsonPrimitive.content)
    }

    @Test
    fun `list_file_roots returns the last known roots when the app does not answer`() = runBlocking {
        filesRepository.isConnected = false
        assertEquals(true, server.callTool("list_file_roots").isError)

        filesRepository.isConnected = true
        server.callTool("list_file_roots").json()
        filesRepository.isConnected = false

        val result = server.callTool("list_file_roots").json()
        assertEquals(true, result["fromCache"]!!.jsonPrimitive.boolean)
        assertEquals(3, result["roots"]!!.jsonArray.size)
    }

    @Test
    fun `list_files lists the first root by default, directories first`() = runBlocking {
        val result = server.callTool("list_files").json()

        assertEquals(false, result["fromCache"]!!.jsonPrimitive.boolean)
        assertEquals(
            listOf("images", "avatar.png", "notes.txt"),
            result["files"]!!.jsonArray.map { it.jsonObject["name"]!!.jsonPrimitive.content },
        )
    }

    @Test
    fun `list_files of a root registered by the app`() = runBlocking {
        val result = server.callTool("list_files", mapOf("path" to "/data/files/exports")).json()

        assertEquals("/data/files/exports/report.csv", result["files"]!!.jsonArray.single().jsonObject["path"]!!.jsonPrimitive.content)
        assertEquals(FilePathDomainModel.Root("/data/files/exports"), filesRepository.lastRefreshedPath)
    }

    @Test
    fun `list_files of a sub directory`() = runBlocking {
        val result = server.callTool("list_files", mapOf("path" to "/data/files/images")).json()

        assertEquals("/data/files/images/cat.png", result["files"]!!.jsonArray.single().jsonObject["path"]!!.jsonPrimitive.content)
    }

    @Test
    fun `list_files returns the cached content when the app does not answer`() = runBlocking {
        server.callTool("list_files").json()
        filesRepository.isConnected = false

        val result = server.callTool("list_files").json()

        assertEquals(true, result["fromCache"]!!.jsonPrimitive.boolean)
        assertEquals(3, result["files"]!!.jsonArray.size)

        val neverListed = server.callTool("list_files", mapOf("path" to "caches"))
        assertEquals(true, neverListed.isError)
    }

    @Test
    fun `list_files refuses relative paths`() = runBlocking {
        val result = server.callTool("list_files", mapOf("path" to "images"))

        assertEquals(true, result.isError)
        assertTrue(result.text().contains("absolute"))
        assertTrue(result.text().contains("\"caches\""))
    }

    @Test
    fun `read_file requires a path`() = runBlocking {
        val result = server.callTool("read_file")

        assertEquals(true, result.isError)
        assertTrue(result.text().contains("path"))
    }

    @Test
    fun `read_file returns the text content`() = runBlocking {
        val result = server.callTool("read_file", mapOf("path" to "/data/files/notes.txt")).json()

        assertEquals("text", result["type"]!!.jsonPrimitive.content)
        assertEquals("hello flocon", result["content"]!!.jsonPrimitive.content)
        assertEquals(false, result["truncated"]!!.jsonPrimitive.boolean)
        assertTrue(File(result["localPath"]!!.jsonPrimitive.content).exists())
    }

    @Test
    fun `read_file truncates long content`() = runBlocking {
        val result = server.callTool("read_file", mapOf("path" to "/data/files/big.json", "maxChars" to 100)).json()

        assertEquals(100, result["content"]!!.jsonPrimitive.content.length)
        assertEquals(true, result["truncated"]!!.jsonPrimitive.boolean)
    }

    @Test
    fun `read_file returns images as images`() = runBlocking {
        val result = server.callTool("read_file", mapOf("path" to "/data/files/avatar.png"))

        assertEquals(false, result.isError)
        val metadata = Json.parseToJsonElement((result.content[0] as TextContent).text).jsonObject
        assertEquals("image", metadata["type"]!!.jsonPrimitive.content)
        val image = result.content[1] as ImageContent
        assertEquals("image/png", image.mimeType)
        assertEquals(5, Base64.getDecoder().decode(image.data).size)
    }

    @Test
    fun `read_file detects images without extension`() = runBlocking {
        val result = server.callTool("read_file", mapOf("path" to "/data/cache/a1b2.1"))

        assertEquals(false, result.isError)
        assertEquals("image/webp", (result.content[1] as ImageContent).mimeType)
    }

    @Test
    fun `read_file does not return the binary content`() = runBlocking {
        val result = server.callTool("read_file", mapOf("path" to "/data/files/app.db")).json()

        assertEquals("binary", result["type"]!!.jsonPrimitive.content)
        assertNull(result["content"])
        assertTrue(result["hint"]!!.jsonPrimitive.content.contains("query_database"))
    }

    @Test
    fun `read_file when the app does not send the file`() = runBlocking {
        val result = server.callTool("read_file", mapOf("path" to "/data/files/unknown"))

        assertEquals(true, result.isError)
        assertTrue(result.text().contains("did not send '/data/files/unknown'"))
    }

    private fun file(path: String, isDirectory: Boolean = false) = FileDomainModel(
        name = path.substringAfterLast('/'),
        isDirectory = isDirectory,
        path = FilePathDomainModel.Real(path),
        size = 10,
        lastModified = Instant.fromEpochMilliseconds(1_000),
    )
}

/**
 * Like the real implementation: the refresh asks the app and stores its answer, observed by [observeFolderContent]
 */
private class FakeFilesRepository(
    private val deviceRoots: List<FileRootDomainModel>,
    private val deviceContent: Map<FilePathDomainModel, List<FileDomainModel>>,
    private val deviceFiles: Map<String, ByteArray>,
    private val downloadDir: File,
) : FilesRepository {

    var isConnected = true
    var lastRefreshedPath: FilePathDomainModel? = null
    private val stored = MutableStateFlow<Map<FilePathDomainModel, List<FileDomainModel>>>(emptyMap())
    private val storedRoots = MutableStateFlow<List<FileRootDomainModel>>(emptyList())

    override fun observeRoots(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<List<FileRootDomainModel>> = storedRoots

    override suspend fun refreshRoots(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Either<Throwable, Unit> {
        if (!isConnected) return Failure(Exception("timeout"))
        storedRoots.value = deviceRoots
        return Success(Unit)
    }

    override fun observeFolderContent(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        path: FilePathDomainModel,
    ): Flow<List<FileDomainModel>> = stored.map { it[path].orEmpty() }

    override suspend fun refreshFolderContent(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        path: FilePathDomainModel,
    ): Either<Throwable, Unit> {
        if (!isConnected) return Failure(Exception("timeout"))
        lastRefreshedPath = path
        stored.value += path to deviceContent[path].orEmpty()
        return Success(Unit)
    }

    override suspend fun downloadFile(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        path: String,
    ): Either<Throwable, FloconReceivedFileDomainModel> {
        val content = deviceFiles[path]?.takeIf { isConnected } ?: return Failure(Exception("timeout"))
        val local = File(downloadDir, path.substringAfterLast('/')).apply { writeBytes(content) }
        return Success(
            FloconReceivedFileDomainModel(
                deviceId = deviceIdAndPackageName.deviceId,
                remotePath = path,
                localPath = local.absolutePath,
                requestId = "request",
                appPackageName = deviceIdAndPackageName.packageName,
                appInstance = deviceIdAndPackageName.appInstance,
            ),
        )
    }

    override suspend fun deleteFolderContent(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, path: FilePathDomainModel) = error("not used")

    override suspend fun deleteFile(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        parentPath: FilePathDomainModel,
        path: FilePathDomainModel,
    ) = error("not used")

    override suspend fun deleteFiles(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        parentPath: FilePathDomainModel,
        paths: List<FilePathDomainModel>,
    ) = error("not used")

    override fun observeWithFoldersSize(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel): Flow<Boolean> = flowOf(false)

    override suspend fun saveWithFoldersSize(deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel, value: Boolean) = error("not used")
}
