package io.github.openflocon.flocon.plugins.files

import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconFile
import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.plugins.files.model.fromdevice.FileDataModel
import java.io.File

internal class FileDataSourceAndroid(
    private val context: FloconContext,
) : FileDataSource {
    override fun getFile(
        path: String,
        isConstantPath: Boolean
    ): FloconFile? {
        val file = if (isConstantPath) {
            when (path) {
                "caches" -> context.appContext.cacheDir
                "files" -> context.appContext.filesDir
                else -> File(path)
            }
        } else {
            File(path)
        }
        return file.takeIf { it.exists() }?.let { FloconFile(it) }
    }

    override fun getFolderContent(path: String, isConstantPath: Boolean): List<FileDataModel> {
        val directory = getFile(path = path, isConstantPath = isConstantPath)

        val directoryFile = directory?.file
        if (directoryFile == null || !directoryFile.exists() || !directoryFile.isDirectory) {
            FloconLogger.logError("file '$path' does not exists.", throwable = null)
            return emptyList()
        }

        val childrenFiles = directoryFile.listFiles() ?: return emptyList()

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


    override fun deleteFile(path: String) {
        try {
            File(path).delete()
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    override fun deleteFolderContent(folder: FloconFile) {
        deleteFolderContent(folder.file)
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
}

internal actual fun fileDataSource(context: FloconContext) : FileDataSource = FileDataSourceAndroid(context)