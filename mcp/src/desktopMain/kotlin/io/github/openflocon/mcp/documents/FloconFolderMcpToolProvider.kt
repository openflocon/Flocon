package io.github.openflocon.mcp.documents

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.common.Success
import io.github.openflocon.domain.device.usecase.GetFloconDocumentPathUseCase
import io.github.openflocon.mcp.McpDescription
import io.github.openflocon.mcp.McpToolException
import io.github.openflocon.mcp.McpToolProvider
import io.github.openflocon.mcp.McpToolServer
import io.github.openflocon.mcp.addTypedTool
import io.github.openflocon.mcp.writeToolAnnotations
import kotlinx.serialization.Serializable
import java.awt.Desktop
import java.io.File

@Serializable
internal data class OpenFloconFolderInput(
    @McpDescription("Optional sub folder of the Flocon documents folder to open, ex: Screenshot or ScreenRecord. Defaults to the documents folder.")
    val subfolder: String? = null,
)

@Serializable
internal data class OpenFloconFolderMcpResult(
    val path: String,
)

/**
 * Opens a folder in the file manager of this computer
 */
internal fun interface FolderOpener {
    fun open(folder: File): Either<Throwable, Unit>
}

internal class DesktopFolderOpener : FolderOpener {
    override fun open(folder: File): Either<Throwable, Unit> = try {
        if (!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
            Failure(UnsupportedOperationException("Opening a folder is not supported on this computer"))
        } else {
            Desktop.getDesktop().open(folder)
            Success(Unit)
        }
    } catch (t: Throwable) {
        Failure(t)
    }
}

internal class FloconFolderMcpToolProvider(
    private val getFloconDocumentPathUseCase: GetFloconDocumentPathUseCase,
    private val folderOpener: FolderOpener,
) : McpToolProvider {

    override fun register(server: McpToolServer) {
        server.addTypedTool(
            name = "open_flocon_folder",
            title = "Open Flocon folder",
            description = "Opens the Flocon documents folder (where screenshots, screen records and exports are saved) " +
                "in the file manager of the computer, and returns its path.",
            annotations = writeToolAnnotations(destructive = false, idempotent = true),
            handler = ::openFolder,
        )
    }

    private fun openFolder(input: OpenFloconFolderInput): OpenFloconFolderMcpResult {
        val root = getFloconDocumentPathUseCase()
        val subfolder = input.subfolder?.trim()?.trimEnd('/', '\\')?.takeIf { it.isNotEmpty() }
        if (subfolder != null) {
            val invalid = File(subfolder).isAbsolute || subfolder.startsWith('/') || subfolder.startsWith('\\') ||
                subfolder.split('/', '\\').any { it == ".." || it == "." } ||
                subfolder.contains(':')
            if (invalid) throw McpToolException("subfolder must be a relative path inside the Flocon documents folder, ex: Screenshot")
        }
        val folder = if (subfolder == null) root else File(root, subfolder)
        if (!folder.exists()) folder.mkdirs()

        folderOpener.open(folder).fold(
            doOnFailure = { throw McpToolException("Unable to open the folder: ${it.message ?: it::class.simpleName}") },
            doOnSuccess = {},
        )
        return OpenFloconFolderMcpResult(path = folder.absolutePath)
    }
}
