package io.github.openflocon.flocon.pluginsold.files

import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconFile
import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.dsl.FloconMarker
import io.github.openflocon.flocon.plugins.files.FileDataSource
import io.github.openflocon.flocon.plugins.files.model.fromdevice.FileDataModel
import java.io.File

internal class FileDataSourceAndroid(
    private val context: FloconContext,
) : FileDataSource {

    @FloconMarker
    override fun getFile(
        path: String,
        isConstantPath: Boolean
    ): FloconFile? {
        val file = if (isConstantPath) {
            when (path) {
                "caches" -> context.context.cacheDir
                "files" -> context.context.filesDir
                else -> File(path)
            }
        } else {
            File(path)
        }
        return file.takeIf { it.exists() }?.let { FloconFile(it) }
    }

    @FloconMarker
    override fun getFolderContent(
        path: String,
        isConstantPath: Boolean,
        withFoldersSize: Boolean,
    ): List<FileDataModel> {
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
                size = if (file.isFile) file.length() else getFolderSize(file, withFoldersSize),
                lastModified = file.lastModified()
            )
        }
    }

    private fun getFolderSize(file: File, withFoldersSize: Boolean): Long {
        return if (withFoldersSize) {
            file.walk()
                .filter { it.isFile }
                .map { it.length() }
                .sum()
        } else 0L
    }


    override fun deleteFile(path: String) {
        deleteInternal(path)
    }

    override fun deleteFiles(path: List<String>) {
        path.forEach {
            deleteInternal(it)
        }
    }

    private fun deleteInternal(path: String) {
        deleteInternal(File(path))
    }

    private fun deleteInternal(file: File) {
        try {
            if (!file.exists()) return

            file.deleteRecursively()
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    @FloconMarker
    override fun deleteFolderContent(folder: FloconFile) {
        deleteFolderContent(folder.file)
    }

    private fun deleteFolderContent(folder: File) {
        if (folder.isDirectory) {
            folder.listFiles()?.forEach { file ->
                deleteInternal(file)
            }
        }
    }
}

//internal actual fun fileDataSource(context: FloconContext) : FileDataSource = FileDataSourceAndroid(context)