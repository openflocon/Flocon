@file:OptIn(ExperimentalForeignApi::class)

package io.github.openflocon.flocon.plugins.files

import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconFile
import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.plugins.files.model.fromdevice.FileDataModel
import kotlinx.cinterop.BooleanVar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSDate
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSFileModificationDate
import platform.Foundation.NSFileSize
import platform.Foundation.NSNumber
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask
import platform.Foundation.timeIntervalSince1970

internal actual fun fileDataSource(context: FloconContext): FileDataSource {
    return FileDataSourceIOs()
}

internal class FileDataSourceIOs : FileDataSource {

    private val fileManager = NSFileManager.defaultManager

    override fun getFile(
        path: String,
        isConstantPath: Boolean
    ): FloconFile? {
        val resolved = if (isConstantPath) {
            when (path) {
                "caches" -> searchPath(NSCachesDirectory)
                "files" -> searchPath(NSDocumentDirectory)
                else -> path
            }
        } else {
            path
        }
        return resolved?.takeIf { fileManager.fileExistsAtPath(it) }?.let { FloconFile(it) }
    }

    override fun getFolderContent(
        path: String,
        isConstantPath: Boolean,
        withFoldersSize: Boolean,
    ): List<FileDataModel> {
        val directory = getFile(path = path, isConstantPath = isConstantPath)?.path
        if (directory == null || !isDirectory(directory)) {
            FloconLogger.logError("file '$path' does not exists.", throwable = null)
            return emptyList()
        }

        val children = fileManager.contentsOfDirectoryAtPath(directory, error = null) ?: return emptyList()
        return children.mapNotNull { it as? String }.map { name ->
            val childPath = "$directory/$name"
            val isDir = isDirectory(childPath)
            FileDataModel(
                name = name,
                isDirectory = isDir,
                path = childPath,
                size = if (isDir) folderSize(childPath, withFoldersSize) else fileSize(childPath),
                lastModified = lastModified(childPath),
            )
        }
    }

    override fun deleteFile(path: String) {
        deleteInternal(path)
    }

    override fun deleteFiles(path: List<String>) {
        path.forEach { deleteInternal(it) }
    }

    override fun deleteFolderContent(folder: FloconFile) {
        if (!isDirectory(folder.path)) return
        fileManager.contentsOfDirectoryAtPath(folder.path, error = null)
            ?.mapNotNull { it as? String }
            ?.forEach { deleteInternal("${folder.path}/$it") }
    }

    private fun deleteInternal(path: String) {
        try {
            if (fileManager.fileExistsAtPath(path)) {
                fileManager.removeItemAtPath(path, error = null)
            }
        } catch (t: Throwable) {
            FloconLogger.logError("could not delete '$path'", t)
        }
    }

    private fun searchPath(directory: ULong): String? =
        NSSearchPathForDirectoriesInDomains(directory, NSUserDomainMask, true).firstOrNull() as? String

    private fun isDirectory(path: String): Boolean = memScoped {
        val isDir = alloc<BooleanVar>()
        fileManager.fileExistsAtPath(path, isDirectory = isDir.ptr) && isDir.value
    }

    private fun fileSize(path: String): Long =
        (fileManager.attributesOfItemAtPath(path, error = null)?.get(NSFileSize) as? NSNumber)?.longLongValue ?: 0L

    private fun lastModified(path: String): Long {
        val date = fileManager.attributesOfItemAtPath(path, error = null)?.get(NSFileModificationDate) as? NSDate
        return date?.let { (it.timeIntervalSince1970 * 1000).toLong() } ?: 0L
    }

    private fun folderSize(path: String, withFoldersSize: Boolean): Long {
        if (!withFoldersSize) return 0L
        val children = fileManager.contentsOfDirectoryAtPath(path, error = null)?.mapNotNull { it as? String } ?: return 0L
        return children.sumOf { name ->
            val childPath = "$path/$name"
            if (isDirectory(childPath)) folderSize(childPath, true) else fileSize(childPath)
        }
    }
}
