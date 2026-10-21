package io.github.openflocon.flocon.plugins.files

import io.github.openflocon.flocon.FloconFile
import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.plugins.files.model.fromdevice.FileDataModel
import io.github.openflocon.flocon.plugins.files.model.fromdevice.FileRootDataModel
import java.io.File

/**
 * [FileDataSource] based on [java.io.File], shared by Android and the JVM.
 *
 * @param defaultRoots the folders of the platform.
 */
internal class JavaIoFileDataSource(
    private val defaultRoots: () -> List<FileRootDataModel>,
) : FileDataSource {

    override fun defaultRoots(): List<FileRootDataModel> = defaultRoots.invoke()

    override fun canonicalPath(path: String): String = File(path).canonicalPath

    override fun isDirectory(path: String): Boolean = File(path).isDirectory

    override fun getFile(path: String): FloconFile? {
        return File(path).takeIf { it.isFile }?.let { FloconFile(it) }
    }

    override fun getFolderContent(
        path: String,
        withFoldersSize: Boolean,
    ): List<FileDataModel> {
        val directoryFile = File(path)
        if (!directoryFile.isDirectory) {
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
                .onEnter { !it.isSymbolicLink() }
                .filter { it.isFile }
                .map { it.length() }
                .sum()
        } else 0L
    }

    override fun delete(path: String) {
        try {
            deleteWithoutFollowingLinks(File(path))
        } catch (t: Throwable) {
            FloconLogger.logError("Cannot delete '$path'", t)
        }
    }

    private fun deleteWithoutFollowingLinks(file: File) {
        if (file.isDirectory && !file.isSymbolicLink()) {
            file.listFiles()?.forEach { deleteWithoutFollowingLinks(it) }
        }
        file.delete()
    }
}

// java.nio.file.Files.isSymbolicLink needs Android API 26
private fun File.isSymbolicLink(): Boolean {
    val parent = absoluteFile.parentFile ?: return false
    val inCanonicalParent = File(parent.canonicalFile, name)
    return inCanonicalParent.canonicalPath != inCanonicalParent.absolutePath
}
