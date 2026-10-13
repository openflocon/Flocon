package io.github.openflocon.mcp.files

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.files.models.FileDomainModel
import io.github.openflocon.domain.files.models.FilePathDomainModel
import io.github.openflocon.domain.files.repository.FilesRepository
import io.github.openflocon.mcp.ARG_DEVICE_ID
import io.github.openflocon.mcp.ARG_PACKAGE_NAME
import io.github.openflocon.mcp.McpTargetResolver
import io.github.openflocon.mcp.McpToolException
import io.github.openflocon.mcp.McpToolProvider
import io.github.openflocon.mcp.intArg
import io.github.openflocon.mcp.integerProperty
import io.github.openflocon.mcp.jsonResult
import io.github.openflocon.mcp.mcpOutputJson
import io.github.openflocon.mcp.properties
import io.github.openflocon.mcp.readOnlyToolAnnotations
import io.github.openflocon.mcp.requiredStringArg
import io.github.openflocon.mcp.safeToolCall
import io.github.openflocon.mcp.stringArg
import io.github.openflocon.mcp.stringProperty
import io.github.openflocon.mcp.targetProperties
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.types.CallToolRequest
import io.modelcontextprotocol.kotlin.sdk.types.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.types.ImageContent
import io.modelcontextprotocol.kotlin.sdk.types.TextContent
import io.modelcontextprotocol.kotlin.sdk.types.ToolSchema
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Base64

internal class FilesMcpToolProvider(
    private val filesRepository: FilesRepository,
    private val targetResolver: McpTargetResolver,
) : McpToolProvider {

    override fun register(server: Server) {
        server.addTool(
            name = "list_files",
            title = "List files",
            description = "Lists the content of a directory of the mobile app: name, absolute path, size, last modification. " +
                "Start with \"$PATH_FILES\" or \"$PATH_CACHES\", then use the absolute path of a sub directory. " +
                "The app must be connected to Flocon, otherwise the last known content is returned.",
            inputSchema = ToolSchema(
                properties = properties {
                    stringProperty(
                        ARG_PATH,
                        "\"$PATH_FILES\" (internal files directory of the app), \"$PATH_CACHES\" (cache directory of the app), " +
                            "or the absolute path of a directory returned by list_files. Default \"$PATH_FILES\".",
                    )
                    targetProperties()
                },
            ),
            toolAnnotations = readOnlyToolAnnotations,
        ) { request ->
            safeToolCall { jsonResult(listFiles(request)) }
        }

        server.addTool(
            name = "read_file",
            title = "Read file",
            description = "Downloads a file of the mobile app and returns its content: text files (json, xml, logs...) as text, " +
                "images as images, and only the metadata for the other binary files. " +
                "The file is also saved on this computer (localPath). The app must be connected to Flocon.",
            inputSchema = ToolSchema(
                properties = properties {
                    stringProperty(ARG_PATH, "Absolute path of the file, from list_files.")
                    integerProperty(
                        ARG_MAX_CHARS,
                        "Text content longer than this is truncated. Default $DEFAULT_MAX_CHARS.",
                        minimum = 100,
                        maximum = MAX_CHARS.toLong(),
                    )
                    targetProperties()
                },
                required = listOf(ARG_PATH),
            ),
            toolAnnotations = readOnlyToolAnnotations,
        ) { request ->
            safeToolCall { readFile(request) }
        }
    }

    private suspend fun listFiles(request: CallToolRequest): FilesMcpResult {
        val target = request.resolveTarget()
        val pathArg = request.stringArg(ARG_PATH) ?: PATH_FILES
        val path = when (pathArg.lowercase()) {
            PATH_FILES -> FilePathDomainModel.Constants.FilesDir
            PATH_CACHES -> FilePathDomainModel.Constants.CachesDir
            else -> FilePathDomainModel.Real(pathArg.requireAbsolute())
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

    private suspend fun readFile(request: CallToolRequest): CallToolResult {
        val target = request.resolveTarget()
        val path = request.requiredStringArg(ARG_PATH).requireAbsolute()
        val maxChars = (request.intArg(ARG_MAX_CHARS) ?: DEFAULT_MAX_CHARS).coerceIn(100, MAX_CHARS)

        val downloaded = filesRepository.downloadFile(deviceIdAndPackageName = target, path = path)
            .fold(
                doOnFailure = { throw McpToolException("$APP_NOT_ANSWERING Also check that '$path' is a file, not a directory.") },
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
        val imageMimeType = imageMimeTypes[file.extension.lowercase()]

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

        // UTF-8 uses up to 4 bytes per char
        val head = file.inputStream().use { it.readNBytes(maxOf(maxChars * 4, BINARY_DETECTION_BYTES)) }
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
                hint = if (truncated) "Truncated content, increase $ARG_MAX_CHARS or open the localPath copy." else null,
            ),
        )
    }

    private fun File.isSqliteDatabase(): Boolean = inputStream().use { it.readNBytes(SQLITE_HEADER.length) }.decodeToString() == SQLITE_HEADER

    private fun String.requireAbsolute(): String = also {
        if (!startsWith("/")) {
            throw McpToolException("'$this' is not an absolute path: use \"$PATH_FILES\", \"$PATH_CACHES\" or a path returned by list_files.")
        }
    }

    private suspend fun CallToolRequest.resolveTarget(): DeviceIdAndPackageNameDomainModel = targetResolver.resolve(
        deviceId = stringArg(ARG_DEVICE_ID),
        packageName = stringArg(ARG_PACKAGE_NAME),
    )

    private companion object {
        const val ARG_PATH = "path"
        const val ARG_MAX_CHARS = "maxChars"

        const val PATH_FILES = "files"
        const val PATH_CACHES = "caches"

        const val TYPE_TEXT = "text"
        const val TYPE_IMAGE = "image"
        const val TYPE_BINARY = "binary"

        const val DEFAULT_MAX_CHARS = 20_000
        const val MAX_CHARS = 200_000
        const val MAX_IMAGE_BYTES = 5L * 1024 * 1024
        const val BINARY_DETECTION_BYTES = 8_000

        const val SQLITE_HEADER = "SQLite format 3\u0000"

        const val APP_NOT_ANSWERING = "The app did not answer: check that it is running and connected to Flocon."

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
        FilePathDomainModel.Constants.FilesDir -> "files"
        FilePathDomainModel.Constants.CachesDir -> "caches"
    },
    isDirectory = isDirectory,
    size = size,
    lastModified = lastModified.toString(),
)
