package com.github.openflocon.flocon.plugins.files

import android.content.Context
import com.github.openflocon.flocon.FloconLogger
import com.github.openflocon.flocon.Protocol
import com.github.openflocon.flocon.core.FloconMessageSender
import com.github.openflocon.flocon.core.FloconPlugin
import com.github.openflocon.flocon.model.FloconMessageFromServer
import com.github.openflocon.flocon.plugins.files.model.fromdevice.FileDataModel
import com.github.openflocon.flocon.plugins.files.model.fromdevice.FilesResultDataModel
import com.github.openflocon.flocon.plugins.files.model.todevice.ToDeviceDeleteFileMessage
import com.github.openflocon.flocon.plugins.files.model.todevice.ToDeviceDeleteFolderContentMessage
import com.github.openflocon.flocon.plugins.files.model.todevice.ToDeviceGetFilesMessage
import java.io.File


interface FloconFilesPlugin : FloconPlugin {
}

class FloconFilesPluginImpl(
    private val context: Context,
) : FloconFilesPlugin {

    override fun onMessageReceived(
        messageFromServer: FloconMessageFromServer,
        sender: FloconMessageSender,
    ) {
        when (messageFromServer.method) {
            Protocol.ToDevice.Files.Method.ListFiles -> {
                val listFilesMessage =
                    ToDeviceGetFilesMessage.fromJson(message = messageFromServer.body) ?: return

                executeGetFile(
                    path = listFilesMessage.path,
                    isConstantPath = listFilesMessage.isConstantPath,
                    requestId = listFilesMessage.requestId,
                    sender = sender,
                )
            }

            Protocol.ToDevice.Files.Method.DeleteFile -> {
                val deleteFilesMessage =
                    ToDeviceDeleteFileMessage.fromJson(message = messageFromServer.body) ?: return

                deleteFile(
                    path = deleteFilesMessage.filePath,
                )

                executeGetFile(
                    path = deleteFilesMessage.parentPath,
                    isConstantPath = deleteFilesMessage.isConstantParentPath,
                    requestId = deleteFilesMessage.requestId,
                    sender = sender,
                )
            }

            Protocol.ToDevice.Files.Method.DeleteFolderContent -> {
                val deleteFolderContentMessage =
                    ToDeviceDeleteFolderContentMessage.fromJson(message = messageFromServer.body)
                        ?: return

                deleteFolderContent(
                    folder = getFile(
                        path = deleteFolderContentMessage.path,
                        isConstantPath = deleteFolderContentMessage.isConstantPath
                    ),
                )

                executeGetFile(
                    path = deleteFolderContentMessage.path,
                    isConstantPath = deleteFolderContentMessage.isConstantPath,
                    requestId = deleteFolderContentMessage.requestId,
                    sender = sender,
                )
            }
        }
    }

    private fun deleteFile(path: String) {
        try {
            File(path).delete()
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    private fun deleteFolderContent(folder: File) {
        if (folder.isDirectory) {
            folder.listFiles()?.forEach { file ->
                if (file.isDirectory) {
                    deleteFolderContent(file)
                } else {
                    try {
                        file.delete()
                    } catch (t: Throwable) {
                        t.printStackTrace()
                    }
                }
            }
        }
    }

    private fun executeGetFile(
        path: String,
        isConstantPath: Boolean,
        requestId: String,
        sender: FloconMessageSender
    ) {
        val files = getFolderContent(
            path = path,
            isConstantPath = isConstantPath,
        )

        sender.send(
            plugin = Protocol.FromDevice.Files.Plugin,
            method = Protocol.FromDevice.Files.Method.ListFiles,
            body = FilesResultDataModel(
                requestId = requestId,
                files = files,
            ).toJson().toString(),
        )
    }

    override fun onConnectedToServer(sender: FloconMessageSender) {
        // no op
    }

    private fun getFile(path: String, isConstantPath: Boolean): File {
        return if (isConstantPath) {
            when (path) {
                "caches" -> context.cacheDir
                "files" -> context.filesDir
                else -> File(path)
            }
        } else {
            File(path)
        }
    }

    private fun getFolderContent(path: String, isConstantPath: Boolean): List<FileDataModel> {
        val directory = getFile(path = path, isConstantPath = isConstantPath)

        if (!directory.exists() || !directory.isDirectory) {
            FloconLogger.logError("file '${directory.absolutePath}' does not exists.", throwable = null)
            return emptyList()
        }

        val childrenFiles = directory.listFiles() ?: return emptyList()

        return childrenFiles.map { file ->
            FileDataModel(
                name = file.name,
                isDirectory = file.isDirectory,
                path = file.absolutePath,
                size = if (file.isFile) file.length() else 0L,
                lastModified = file.lastModified()
            )
        }
    }
}