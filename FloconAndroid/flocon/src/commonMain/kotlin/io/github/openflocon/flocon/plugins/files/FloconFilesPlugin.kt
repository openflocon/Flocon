package io.github.openflocon.flocon.plugins.files

import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconFile
import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconFileSender
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.core.FloconPlugin
import io.github.openflocon.flocon.model.FloconFileInfo
import io.github.openflocon.flocon.model.FloconMessageFromServer
import io.github.openflocon.flocon.plugins.files.model.FloconFolderModel
import io.github.openflocon.flocon.plugins.files.model.fromdevice.FileDataModel
import io.github.openflocon.flocon.plugins.files.model.fromdevice.FileRootDataModel
import io.github.openflocon.flocon.plugins.files.model.fromdevice.FileRootsResultDataModel
import io.github.openflocon.flocon.plugins.files.model.fromdevice.FilesResultDataModel
import io.github.openflocon.flocon.plugins.files.model.todevice.ToDeviceDeleteFileMessage
import io.github.openflocon.flocon.plugins.files.model.todevice.ToDeviceDeleteFilesMessage
import io.github.openflocon.flocon.plugins.files.model.todevice.ToDeviceDeleteFolderContentMessage
import io.github.openflocon.flocon.plugins.files.model.todevice.ToDeviceGetFileMessage
import io.github.openflocon.flocon.plugins.files.model.todevice.ToDeviceGetFilesMessage
import io.github.openflocon.flocon.plugins.files.model.todevice.ToDeviceGetRootsMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

/**
 * File system access of a platform. Every path is absolute:
 * [FloconFilesPluginImpl] checks it is inside a root folder before calling it.
 */
internal interface FileDataSource {
    /** The folders shown without any registration, with a stable slug as id. */
    fun defaultRoots(): List<FileRootDataModel>

    /** [path] with its symbolic links resolved. */
    fun canonicalPath(path: String): String
    fun isDirectory(path: String): Boolean
    fun getFile(path: String): FloconFile?
    fun getFolderContent(path: String, withFoldersSize: Boolean): List<FileDataModel>

    /** Deletes a file, or a folder and its content, without following symbolic links. */
    fun delete(path: String)
}

internal expect fun fileDataSource(context: FloconContext): FileDataSource

internal class FloconFilesPluginImpl(
    private val context: FloconContext,
    private val floconFileSender: FloconFileSender,
    private val sender: FloconMessageSender,
    private val fileDataSource: FileDataSource = fileDataSource(context),
) : FloconPlugin, FloconFilesPlugin {

    private val withFoldersSize = MutableStateFlow(false)

    private val defaultRoots by lazy { fileDataSource.defaultRoots() }
    private val registeredRoots = MutableStateFlow<List<FileRootDataModel>>(emptyList())

    /** The default roots, then the registered ones. */
    internal fun roots(): FileRoots = FileRoots.build(
        defaultRoots = defaultRoots,
        registeredRoots = registeredRoots.value,
        canonicalPath = fileDataSource::canonicalPath,
    )

    override fun register(folder: FloconFolderModel) {
        val path = normalizePath(folder.absolutePath)
        if (path == null) {
            FloconLogger.logError("Folder '${folder.absolutePath}' is not an absolute path", throwable = null)
            return
        }
        val root = FileRootDataModel(id = path, name = folder.displayName, path = path)
        // registering the same folder again renames it
        registeredRoots.update { roots -> roots.filterNot { it.id == root.id } + root }
    }

    override fun onMessageReceived(
        messageFromServer: FloconMessageFromServer,
    ) {
        when (messageFromServer.method) {
            Protocol.ToDevice.Files.Method.GetRoots -> {
                val getRootsMessage = ToDeviceGetRootsMessage.fromJson(message = messageFromServer.body) ?: return

                sendRoots(requestId = getRootsMessage.requestId)
            }

            Protocol.ToDevice.Files.Method.ListFiles -> {
                val listFilesMessage = ToDeviceGetFilesMessage.fromJson(message = messageFromServer.body) ?: return

                withFoldersSize.update { listFilesMessage.withFoldersSize }

                executeGetFile(
                    path = listFilesMessage.path,
                    isConstantPath = listFilesMessage.isConstantPath,
                    requestId = listFilesMessage.requestId,
                )
            }

            Protocol.ToDevice.Files.Method.GetFile -> {
                val getFileMessage = ToDeviceGetFileMessage.fromJson(message = messageFromServer.body) ?: return

                val path = roots().resolve(path = getFileMessage.path, isConstantPath = false)
                if (path == null) {
                    FloconLogger.logError("Refused to send '${getFileMessage.path}': outside the root folders", throwable = null)
                    return
                }
                fileDataSource.getFile(path)?.let { file ->
                    floconFileSender.send(
                        file = file,
                        infos = FloconFileInfo(
                            requestId = getFileMessage.requestId,
                            path = getFileMessage.path,
                        )
                    )
                }
            }

            Protocol.ToDevice.Files.Method.DeleteFile -> {
                val deleteFilesMessage =
                    ToDeviceDeleteFileMessage.fromJson(message = messageFromServer.body) ?: return

                delete(roots(), deleteFilesMessage.filePath)

                executeGetFile(
                    path = deleteFilesMessage.parentPath,
                    isConstantPath = deleteFilesMessage.isConstantParentPath,
                    requestId = deleteFilesMessage.requestId,
                )
            }

            Protocol.ToDevice.Files.Method.DeleteFiles -> {
                val deleteFilesMessage =
                    ToDeviceDeleteFilesMessage.fromJson(message = messageFromServer.body) ?: return

                val roots = roots()
                deleteFilesMessage.filePaths.forEach { delete(roots, it) }

                executeGetFile(
                    path = deleteFilesMessage.parentPath,
                    isConstantPath = deleteFilesMessage.isConstantParentPath,
                    requestId = deleteFilesMessage.requestId,
                )
            }

            Protocol.ToDevice.Files.Method.DeleteFolderContent -> {
                val deleteFolderContentMessage =
                    ToDeviceDeleteFolderContentMessage.fromJson(message = messageFromServer.body)
                        ?: return

                val roots = roots()
                roots.resolve(
                    path = deleteFolderContentMessage.path,
                    isConstantPath = deleteFolderContentMessage.isConstantPath,
                )?.let { folder ->
                    fileDataSource.getFolderContent(path = folder, withFoldersSize = false).forEach {
                        delete(roots, it.path)
                    }
                }

                executeGetFile(
                    path = deleteFolderContentMessage.path,
                    isConstantPath = deleteFolderContentMessage.isConstantPath,
                    requestId = deleteFolderContentMessage.requestId,
                )
            }
        }
    }

    private fun delete(roots: FileRoots, path: String) {
        if (!roots.canDelete(path)) {
            FloconLogger.logError("Refused to delete '$path': outside the root folders or a root folder", throwable = null)
            return
        }
        fileDataSource.delete(path)
    }

    private fun sendRoots(requestId: String) {
        val roots = roots().roots.filter { fileDataSource.isDirectory(it.path) }
        try {
            sender.send(
                plugin = Protocol.FromDevice.Files.Plugin,
                method = Protocol.FromDevice.Files.Method.GetRoots,
                body = FileRootsResultDataModel(
                    requestId = requestId,
                    roots = roots,
                ).toJson(),
            )
        } catch (t: Throwable) {
            FloconLogger.logError("File roots parsing error", t)
        }
    }

    private fun executeGetFile(
        path: String,
        isConstantPath: Boolean,
        requestId: String,
    ) {
        val folder = roots().resolve(path = path, isConstantPath = isConstantPath)
        val files = if (folder == null) {
            FloconLogger.logError("Refused to list '$path': outside the root folders", throwable = null)
            emptyList()
        } else {
            fileDataSource.getFolderContent(
                path = folder,
                withFoldersSize = withFoldersSize.value,
            )
        }

        try {
            sender.send(
                plugin = Protocol.FromDevice.Files.Plugin,
                method = Protocol.FromDevice.Files.Method.ListFiles,
                body = FilesResultDataModel(
                    requestId = requestId,
                    files = files,
                ).toJson(),
            )
        } catch (t: Throwable) {
            FloconLogger.logError("File parsing error", t)
        }
    }

    override fun onConnectedToServer() {
        // no op
    }
}
