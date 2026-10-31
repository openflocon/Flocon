package io.github.openflocon.flocon.plugins.files

import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.FloconConfig
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconFile
import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.FloconPlugin
import io.github.openflocon.flocon.FloconPluginConfig
import io.github.openflocon.flocon.FloconPluginFactory
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.core.FloconFileSender
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.core.decode
import io.github.openflocon.flocon.core.encode
import io.github.openflocon.flocon.error.pluginNotInitialized
import io.github.openflocon.flocon.model.FloconFileInfo
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

class FloconFilesConfig : FloconPluginConfig {
    /**
     * Absolute paths of folders shown in the Files section, named after their last segment.
     */
    val roots = mutableListOf<String>()

    internal val folders = mutableListOf<FloconFolderModel>()

    /**
     * Shows a folder in the Files section of the Flocon desktop app, next to the default ones.
     */
    fun folder(displayName: String, absolutePath: String) {
        folders += FloconFolderModel(displayName = displayName, absolutePath = absolutePath)
    }
}

/**
 * Shows the folders of the app in the Files section of the Flocon desktop app, where their content
 * can be browsed, downloaded and deleted. A root folder itself is never deleted.
 */
interface FloconFilesPlugin : FloconPlugin {
    /**
     * Shows [folder] in the Files section, next to the default folders of the platform.
     *
     * Registering the same folder again replaces its name. A folder that does not exist is not shown.
     */
    fun register(folder: FloconFolderModel)

    fun register(displayName: String, absolutePath: String) {
        register(FloconFolderModel(displayName = displayName, absolutePath = absolutePath))
    }
}

object FloconFiles : FloconPluginFactory<FloconFilesConfig, FloconFilesPlugin> {
    override val name: String = "Files"
    override val pluginId: String = Protocol.ToDevice.Files.Plugin
    override fun createConfig(context: FloconContext) = FloconFilesConfig()
    override fun install(
        pluginConfig: FloconFilesConfig,
        floconConfig: FloconConfig,
        encoder: FloconEncoder
    ): FloconFilesPlugin {
        val client = floconConfig.client
        return FloconFilesPluginImpl(
            context = floconConfig.context,
            floconFileSender = client as FloconFileSender,
            sender = client as FloconMessageSender,
            encoder = encoder
        )
            .also { plugin ->
                pluginConfig.roots.forEach { path ->
                    plugin.register(displayName = path.trimEnd('/').substringAfterLast('/'), absolutePath = path)
                }
                pluginConfig.folders.forEach(plugin::register)
            }
            .also { FloconFilesPluginImpl.plugin = it }
    }
}

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
    private val encoder: FloconEncoder,
    private val fileDataSource: FileDataSource = fileDataSource(context),
) : FloconPlugin, FloconFilesPlugin {

    override val key: String = Protocol.ToDevice.Files.Plugin
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

    override suspend fun onMessageReceived(
        method: String,
        body: String,
    ) {
        when (method) {
            Protocol.ToDevice.Files.Method.GetRoots -> {
                val getRootsMessage = encoder.decode<ToDeviceGetRootsMessage>(body) ?: return

                sendRoots(requestId = getRootsMessage.requestId)
            }

            Protocol.ToDevice.Files.Method.ListFiles -> {
                val listFilesMessage = encoder.decode<ToDeviceGetFilesMessage>(body) ?: return

                withFoldersSize.update { listFilesMessage.withFoldersSize }

                executeGetFile(
                    path = listFilesMessage.path,
                    isConstantPath = listFilesMessage.isConstantPath,
                    requestId = listFilesMessage.requestId,
                )
            }

            Protocol.ToDevice.Files.Method.GetFile -> {
                val getFileMessage = encoder.decode<ToDeviceGetFileMessage>(body) ?: return

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
                val deleteFilesMessage = encoder.decode<ToDeviceDeleteFileMessage>(body) ?: return

                delete(roots(), deleteFilesMessage.filePath)

                executeGetFile(
                    path = deleteFilesMessage.parentPath,
                    isConstantPath = deleteFilesMessage.isConstantParentPath,
                    requestId = deleteFilesMessage.requestId,
                )
            }

            Protocol.ToDevice.Files.Method.DeleteFiles -> {
                val deleteFilesMessage = encoder.decode<ToDeviceDeleteFilesMessage>(body) ?: return

                val roots = roots()
                deleteFilesMessage.filePaths.forEach { delete(roots, it) }

                executeGetFile(
                    path = deleteFilesMessage.parentPath,
                    isConstantPath = deleteFilesMessage.isConstantParentPath,
                    requestId = deleteFilesMessage.requestId,
                )
            }

            Protocol.ToDevice.Files.Method.DeleteFolderContent -> {
                val deleteFolderContentMessage = encoder.decode<ToDeviceDeleteFolderContentMessage>(body) ?: return

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
                body = encoder.encode(
                    FileRootsResultDataModel(
                        requestId = requestId,
                        roots = roots,
                    )
                ),
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
                body = encoder.encode(
                    FilesResultDataModel(
                        requestId = requestId,
                        files = files,
                    )
                ),
            )
        } catch (t: Throwable) {
            FloconLogger.logError("File parsing error", t)
        }
    }

    override suspend fun onConnectedToServer() {
        // no op
    }

    companion object {
        var plugin: FloconFilesPlugin? = null
    }
}

val Flocon.Companion.filesPlugin: FloconFilesPlugin
    get() = FloconFilesPluginImpl.plugin ?: pluginNotInitialized("Files", NoOpFilesPlugin())

private class NoOpFilesPlugin : FloconFilesPlugin {
    override val key: String = ""
    override fun register(folder: FloconFolderModel) = Unit
    override suspend fun onMessageReceived(method: String, body: String) = Unit
    override suspend fun onConnectedToServer() = Unit
}
