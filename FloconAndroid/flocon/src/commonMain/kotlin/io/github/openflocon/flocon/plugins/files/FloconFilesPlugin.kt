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
import io.github.openflocon.flocon.plugins.files.model.fromdevice.FileDataModel
import io.github.openflocon.flocon.plugins.files.model.fromdevice.FilesResultDataModel
import io.github.openflocon.flocon.plugins.files.model.todevice.ToDeviceDeleteFileMessage
import io.github.openflocon.flocon.plugins.files.model.todevice.ToDeviceDeleteFolderContentMessage
import io.github.openflocon.flocon.plugins.files.model.todevice.ToDeviceGetFileMessage
import io.github.openflocon.flocon.plugins.files.model.todevice.ToDeviceGetFilesMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

internal interface FileDataSource {
    fun getFile(path: String, isConstantPath: Boolean): FloconFile?
    fun getFolderContent(path: String, isConstantPath: Boolean, withFoldersSize: Boolean): List<FileDataModel>
    fun deleteFile(path: String)
    fun deleteFolderContent(folder: FloconFile)
}

internal expect fun fileDataSource(context: FloconContext) : FileDataSource

internal class FloconFilesPluginImpl(
    private val context: FloconContext,
    private val floconFileSender: FloconFileSender,
    private val sender: FloconMessageSender,
) : FloconPlugin, FloconFilesPlugin {

    private val fileDataSource = fileDataSource(context)
    private val withFoldersSize = MutableStateFlow(false)

    override fun onMessageReceived(
        messageFromServer: FloconMessageFromServer,
    ) {
        when (messageFromServer.method) {
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

                fileDataSource.getFile(path = getFileMessage.path, isConstantPath = false)?.let { file ->
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

                fileDataSource.deleteFile(
                    path = deleteFilesMessage.filePath,
                )

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