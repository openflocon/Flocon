package io.github.openflocon.flocon.plugins.files

import io.github.openflocon.flocon.FloconConfig
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconFile
import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.FloconPlugin
import io.github.openflocon.flocon.FloconPluginConfig
import io.github.openflocon.flocon.FloconPluginFactory
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconFileSender
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.dsl.FloconMarker
import io.github.openflocon.flocon.plugins.files.model.fromdevice.FileDataModel
import io.github.openflocon.flocon.plugins.files.model.todevice.ToDeviceDeleteFileMessage
import io.github.openflocon.flocon.plugins.files.model.todevice.ToDeviceDeleteFilesMessage
import io.github.openflocon.flocon.plugins.files.model.todevice.ToDeviceDeleteFolderContentMessage
import io.github.openflocon.flocon.plugins.files.model.todevice.ToDeviceGetFileMessage
import io.github.openflocon.flocon.plugins.files.model.todevice.ToDeviceGetFilesMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FloconFilesConfig : FloconPluginConfig {
    val roots = mutableListOf<String>()
}

interface FloconFilesPlugin : FloconPlugin

object FloconFiles : FloconPluginFactory<FloconFilesConfig, FloconFilesPlugin> {
    override val name: String = "Files"
    override val pluginId: String = Protocol.ToDevice.Files.Plugin
    override fun createConfig(context: FloconContext) = FloconFilesConfig()
    override fun install(
        pluginConfig: FloconFilesConfig,
        floconConfig: FloconConfig
    ): FloconFilesPlugin {
        val client = floconConfig.client
        return FloconFilesPluginImpl(
            context = floconConfig.context,
            floconFileSender = client as FloconFileSender,
            sender = client as FloconMessageSender
        )
    }
}

internal interface FileDataSource {

    @FloconMarker
    fun getFile(path: String, isConstantPath: Boolean): FloconFile?

    fun getFolderContent(
        path: String,
        isConstantPath: Boolean,
        withFoldersSize: Boolean
    ): List<FileDataModel>

    fun deleteFile(path: String)
    fun deleteFiles(path: List<String>)

    @FloconMarker
    fun deleteFolderContent(folder: FloconFile)
}

internal expect fun fileDataSource(context: FloconContext): FileDataSource

internal class FloconFilesPluginImpl(
    private val context: FloconContext,
    private val floconFileSender: FloconFileSender,
    private val sender: FloconMessageSender,
) : FloconPlugin, FloconFilesPlugin {

    override val key: String = "FILES"
    private val fileDataSource = fileDataSource(context)
    private val withFoldersSize = MutableStateFlow(false)

    @FloconMarker
    override suspend fun onMessageReceived(
        method: String,
        body: String,
    ) {
        when (method) {
            Protocol.ToDevice.Files.Method.ListFiles -> {
                val listFilesMessage = ToDeviceGetFilesMessage.fromJson(message = body) ?: return

                withFoldersSize.update { listFilesMessage.withFoldersSize }

                executeGetFile(
                    path = listFilesMessage.path,
                    isConstantPath = listFilesMessage.isConstantPath,
                    requestId = listFilesMessage.requestId,
                )
            }

            Protocol.ToDevice.Files.Method.GetFile -> {
                val getFileMessage = ToDeviceGetFileMessage.fromJson(message = body) ?: return

                fileDataSource.getFile(path = getFileMessage.path, isConstantPath = false)
                    ?.let { file ->
//                        floconFileSender.send(
//                            file = file,
//                            infos = FloconFileInfo(
//                                requestId = getFileMessage.requestId,
//                                path = getFileMessage.path,
//                            )
//                        )
                    }
            }

            Protocol.ToDevice.Files.Method.DeleteFile -> {
                val deleteFilesMessage =
                    ToDeviceDeleteFileMessage.fromJson(message = body) ?: return

                fileDataSource.deleteFile(
                    path = deleteFilesMessage.filePath,
                )

                executeGetFile(
                    path = deleteFilesMessage.parentPath,
                    isConstantPath = deleteFilesMessage.isConstantParentPath,
                    requestId = deleteFilesMessage.requestId,
                )
            }

            Protocol.ToDevice.Files.Method.DeleteFiles -> {
                val deleteFilesMessage =
                    ToDeviceDeleteFilesMessage.fromJson(message = body) ?: return

                fileDataSource.deleteFiles(
                    path = deleteFilesMessage.filePaths,
                )

                executeGetFile(
                    path = deleteFilesMessage.parentPath,
                    isConstantPath = deleteFilesMessage.isConstantParentPath,
                    requestId = deleteFilesMessage.requestId,
                )
            }

            Protocol.ToDevice.Files.Method.DeleteFolderContent -> {
                val deleteFolderContentMessage =
                    ToDeviceDeleteFolderContentMessage.fromJson(message = body)
                        ?: return

                fileDataSource.getFile(
                    path = deleteFolderContentMessage.path,
                    isConstantPath = deleteFolderContentMessage.isConstantPath
                )?.let { folder ->
                    fileDataSource.deleteFolderContent(
                        folder
                    )
                }

                executeGetFile(
                    path = deleteFolderContentMessage.path,
                    isConstantPath = deleteFolderContentMessage.isConstantPath,
                    requestId = deleteFolderContentMessage.requestId,
                )
            }
        }
    }

    private fun executeGetFile(
        path: String,
        isConstantPath: Boolean,
        requestId: String,
    ) {
        val files = fileDataSource.getFolderContent(
            path = path,
            isConstantPath = isConstantPath,
            withFoldersSize = withFoldersSize.value,
        )

        try {
//            sender.send(
//                plugin = Protocol.FromDevice.Files.Plugin,
//                method = Protocol.FromDevice.Files.Method.ListFiles,
//                body = FilesResultDataModel(
//                    requestId = requestId,
//                    files = files,
//                ).toJson(),
//            )
        } catch (t: Throwable) {
            FloconLogger.logError("File parsing error", t)
        }
    }

    override suspend fun onConnectedToServer() {
        // no op
    }
}