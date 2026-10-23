package io.github.openflocon.mcp.files

import io.github.openflocon.domain.files.models.FileDomainModel
import io.github.openflocon.domain.files.models.FilePathDomainModel
import io.github.openflocon.domain.files.models.FileRootDomainModel
import io.github.openflocon.domain.files.repository.FilesRepository
import io.github.openflocon.mcp.McpTargetResolver
import io.github.openflocon.mcp.McpToolException
import io.github.openflocon.mcp.McpToolProvider
import io.github.openflocon.mcp.McpToolServer
import io.github.openflocon.mcp.addTypedTool
import io.github.openflocon.mcp.addTypedToolWithResult
import io.github.openflocon.mcp.jsonResult
import io.github.openflocon.mcp.mcpOutputJson
import io.github.openflocon.mcp.readOnlyToolAnnotations
import io.modelcontextprotocol.kotlin.sdk.types.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.types.ImageContent
import io.modelcontextprotocol.kotlin.sdk.types.TextContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Base64

internal class FilesMcpToolProvider(
    private val filesRepository: FilesRepository,
    private val targetResolver: McpTargetResolver,
) : McpToolProvider {

    override fun register(server: McpToolServer) {
        server.addTypedTool(
            name = "list_file_roots",
            title = "List file roots",
            description = "Lists the root folders announced by the mobile app (id, name, absolute path): " +
                "the default folders of the platform (ex: files and caches on Android, documents, caches or tmp on iOS) " +
                "and the folders registered by the app. Use an id with list_files. " +
                "The app must be connected to Flocon, otherwise the last known roots are returned.",
            annotations = readOnlyToolAnnotations,
            handler = ::listFileRoots,
        )

        server.addTypedTool(
            name = "list_files",
            title = "List files",
            description = "Lists the content of a directory of the mobile app: name, absolute path, size, last modification. " +
                "Start with the id of a root folder (see list_file_roots), then use the absolute path of a sub directory. " +
                "The app must be connected to Flocon, otherwise the last known content is returned.",
            annotations = readOnlyToolAnnotations,
            handler = ::listFiles,
        )

        server.addTypedToolWithResult(
            name = "read_file",
            title = "Read file",
            description = "Downloads a file of the mobile app and returns its content: text files (json, xml, logs...) as text, " +
                "images as images, and only the metadata for the other binary files. " +
                "The file is also saved on this computer (localPath). The app must be connected to Flocon.",
            annotations = readOnlyToolAnnotations,
            handler = ::readFile,
        )
    }

    private suspend fun listFileRoots(input: ListFileRootsInput): FileRootsMcpResult {
        val target = targetResolver.resolve(input)

        val refreshError = filesRepository.refreshRoots(deviceIdAndPackageName = target)
            .fold(doOnFailure = { it }, doOnSuccess = { null })
        val roots = filesRepository.observeRoots(deviceIdAndPackageName = target).first()

        if (refreshError != null && roots.isEmpty()) {
            throw McpToolException(APP_NOT_ANSWERING)
        }

        return FileRootsMcpResult(
            deviceId = target.deviceId,
            packageName = target.packageName,
            fromCache = refreshError != null,
            warning = refreshError?.let { "$APP_NOT_ANSWERING Returning the last known roots." },
            roots = roots.map { it.toMcpModel() },
        )
    }

    private suspend fun listFiles(input: ListFilesInput): FilesMcpResult {
        val target = targetResolver.resolve(input)
        // the roots are announced by the app: ask for them when they are not known yet
        val roots = filesRepository.observeRoots(deviceIdAndPackageName = target).first()
            .ifEmpty {
                filesRepository.refreshRoots(deviceIdAndPackageName = target)
                filesRepository.observeRoots(deviceIdAndPackageName = target).first()
            }
        val pathArg = input.path.takeIf { it.isNotBlank() }
            ?: roots.firstOrNull()?.id
            ?: throw McpToolException("No root folder is known for this app: $APP_NOT_ANSWERING")
        val path = if (roots.any { it.id == pathArg }) {
            FilePathDomainModel.Root(pathArg)
        } else {
            FilePathDomainModel.Real(pathArg.requireAbsolute(roots))
        }

        // the refresh stores the content returned by the app, read just after
        val refreshError = filesRepository.refreshFolderContent(deviceIdAndPackageName = target, path = path)
            .fold(doOnFailure = { it }, doOnSuccess = { null })
        val files = filesRepository.observeFolderContent(deviceIdAndPackageName = target, path = path).first()

        if (refreshError != null && files.isEmpty()) {
            throw McpToolException(APP_NOT_ANSWERING)
        }

        return FilesMcpResult(
            deviceId = target.deviceId,
            packageName = target.packageName,
            path = pathArg,
            fromCache = refreshError != null,
            warning = refreshError?.let { "$APP_NOT_ANSWERING Returning the last known content." },
            files = files
                .sortedWith(compareBy<FileDomainModel> { !it.isDirectory }.thenBy { it.name.lowercase() })
                .map { it.toMcpModel() },
        )
    }

    private suspend fun readFile(input: ReadFileInput): CallToolResult {
        val target = targetResolver.resolve(input)
        val path = input.path.requireAbsolute()
        val maxChars = input.maxChars.coerceIn(100, MAX_CHARS)

        val downloaded = filesRepository.downloadFile(deviceIdAndPackageName = target, path = path)
            .fold(
                doOnFailure = {
                    throw McpToolException(
                        "The app did not send '$path': check that this file exists (not a directory, see list_files) " +
                            "and that the app is connected to Flocon.",
                    )
                },
                doOnSuccess = { File(it.localPath) },
            )

        return withContext(Dispatchers.IO) { readLocalFile(remotePath = path, file = downloaded, maxChars = maxChars) }
    }

    private fun readLocalFile(
        remotePath: String,
        file: File,
        maxChars: Int,
    ): CallToolResult {
        val size = file.length()
        // UTF-8 uses up to 4 bytes per char
        val head = file.inputStream().use { it.readNBytes(maxOf(maxChars * 4, BINARY_DETECTION_BYTES)) }
        // cached images often have no extension (ex: OkHttp cache entries)
        val imageMimeType = head.imageMimeType() ?: imageMimeTypes[file.extension.lowercase()]

        if (imageMimeType != null && size <= MAX_IMAGE_BYTES) {
            val metadata = FileContentMcpModel(
                path = remotePath,
                size = size,
                type = TYPE_IMAGE,
                mimeType = imageMimeType,
                content = null,
                truncated = false,
                localPath = file.absolutePath,
                hint = null,
            )
            return CallToolResult(
                content = listOf(
                    TextContent(text = mcpOutputJson.encodeToString(metadata)),
                    ImageContent(data = Base64.getEncoder().encodeToString(file.readBytes()), mimeType = imageMimeType),
                ),
                isError = false,
            )
        }

        val isBinary = head.take(BINARY_DETECTION_BYTES).contains(0.toByte()) || imageMimeType != null
        if (isBinary) {
            return jsonResult(
                FileContentMcpModel(
                    path = remotePath,
                    size = size,
                    type = TYPE_BINARY,
                    mimeType = imageMimeType,
                    content = null,
                    truncated = false,
                    localPath = file.absolutePath,
                    hint = if (file.isSqliteDatabase()) {
                        "This is a SQLite database: use list_databases and query_database to read it."
                    } else {
                        "Binary file: its content is not returned, open the localPath copy to inspect it."
                    },
                ),
            )
        }

        val text = head.decodeToString()
        val truncated = text.length > maxChars || head.size.toLong() < size
        return jsonResult(
            FileContentMcpModel(
                path = remotePath,
                size = size,
                type = TYPE_TEXT,
                mimeType = null,
                content = text.take(maxChars),
                truncated = truncated,
                localPath = file.absolutePath,
                hint = if (truncated) "Truncated content, increase maxChars or open the localPath copy." else null,
            ),
        )
    }

    private fun ByteArray.imageMimeType(): String? = when {
        startsWith(PNG_SIGNATURE) -> "image/png"
        startsWith(JPEG_SIGNATURE) -> "image/jpeg"
        startsWith("GIF87a".encodeToByteArray()) || startsWith("GIF89a".encodeToByteArray()) -> "image/gif"
        size >= 12 && startsWith("RIFF".encodeToByteArray()) && copyOfRange(8, 12).contentEquals("WEBP".encodeToByteArray()) -> "image/webp"
        else -> null
    }

    private fun ByteArray.startsWith(prefix: ByteArray): Boolean = size >= prefix.size && copyOfRange(0, prefix.size).contentEquals(prefix)

    private fun File.isSqliteDatabase(): Boolean = inputStream().use { it.readNBytes(SQLITE_HEADER.length) }.decodeToString() == SQLITE_HEADER

    private fun String.requireAbsolute(): String = also {
        if (!startsWith("/")) {
            throw McpToolException("'$this' is not an absolute path: use a path returned by list_files.")
        }
    }

    private fun String.requireAbsolute(roots: List<FileRootDomainModel>): String = also {
        if (!startsWith("/")) {
            throw McpToolException(
                "'$this' is neither a root folder nor an absolute path: use one of the root ids " +
                    roots.joinToString { "\"${it.id}\"" } + " (see list_file_roots) or a path returned by list_files.",
            )
        }
    }

    private companion object {
        const val TYPE_TEXT = "text"
        const val TYPE_IMAGE = "image"
        const val TYPE_BINARY = "binary"

        const val MAX_IMAGE_BYTES = 5L * 1024 * 1024
        const val BINARY_DETECTION_BYTES = 8_000

        const val SQLITE_HEADER = "SQLite format 3\u0000"

        const val APP_NOT_ANSWERING = "The app did not answer: check that it is running and connected to Flocon."

        val PNG_SIGNATURE = byteArrayOf(0x89.toByte(), 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A)
        val JPEG_SIGNATURE = byteArrayOf(0xFF.toByte(), 0xD8.toByte(), 0xFF.toByte())

        val imageMimeTypes = mapOf(
            "png" to "image/png",
            "jpg" to "image/jpeg",
            "jpeg" to "image/jpeg",
            "gif" to "image/gif",
            "webp" to "image/webp",
        )
    }
}

private fun FileDomainModel.toMcpModel() = FileMcpModel(
    name = name,
    path = when (val path = path) {
        is FilePathDomainModel.Real -> path.absolutePath
        is FilePathDomainModel.Root -> path.id
    },
    isDirectory = isDirectory,
    size = size,
    lastModified = lastModified.toString(),
)

private fun FileRootDomainModel.toMcpModel() = FileRootMcpModel(
    id = id,
    name = name,
    path = path,
)
