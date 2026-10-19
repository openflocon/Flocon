package io.github.openflocon.flocon.plugins.files

import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconFile
import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.plugins.files.model.fromdevice.FileDataModel
import io.github.openflocon.flocon.plugins.files.model.fromdevice.FileRootDataModel
import kotlinx.cinterop.BooleanVar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSDate
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSFileModificationDate
import platform.Foundation.NSFileSize
import platform.Foundation.NSFileType
import platform.Foundation.NSFileTypeRegular
import platform.Foundation.NSHomeDirectory
import platform.Foundation.NSNumber
import platform.Foundation.NSSearchPathDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import platform.Foundation.timeIntervalSince1970

internal actual fun fileDataSource(context: FloconContext): FileDataSource {
    return FileDataSourceIOs()
}

@OptIn(ExperimentalForeignApi::class)
internal class FileDataSourceIOs : FileDataSource {

    private val fileManager get() = NSFileManager.defaultManager

    // same folders as FloconSwift
    override fun defaultRoots(): List<FileRootDataModel> = listOfNotNull(
        searchPathDirectory(NSDocumentDirectory)?.let { FileRootDataModel(id = "documents", name = "Documents", path = it) },
        searchPathDirectory(NSApplicationSupportDirectory)?.let { FileRootDataModel(id = "applicationSupport", name = "Application Support", path = it) },
        searchPathDirectory(NSCachesDirectory)?.let { FileRootDataModel(id = "caches", name = "Caches", path = it) },
        FileRootDataModel(id = "tmp", name = "tmp", path = NSTemporaryDirectory().trimEnd('/')),
        FileRootDataModel(id = "container", name = "App container", path = NSHomeDirectory()),
    )

    private fun searchPathDirectory(directory: NSSearchPathDirectory): String? {
        return NSSearchPathForDirectoriesInDomains(directory, NSUserDomainMask, true).firstOrNull() as? String
    }

    override fun canonicalPath(path: String): String {
        return NSURL.fileURLWithPath(path).URLByResolvingSymlinksInPath?.path ?: path
    }

    override fun isDirectory(path: String): Boolean = memScoped {
        val isDirectory = alloc<BooleanVar>()
        fileManager.fileExistsAtPath(path, isDirectory = isDirectory.ptr) && isDirectory.value
    }

    override fun getFile(path: String): FloconFile? {
        if (!fileManager.fileExistsAtPath(path) || isDirectory(path)) return null
        return FloconFile(path)
    }

    override fun getFolderContent(
        path: String,
        withFoldersSize: Boolean,
    ): List<FileDataModel> {
        if (!isDirectory(path)) {
            FloconLogger.logError("file '$path' does not exists.", throwable = null)
            return emptyList()
        }

        val names = fileManager.contentsOfDirectoryAtPath(path, error = null) ?: return emptyList()

        return names.filterIsInstance<String>().map { name ->
            val childPath = "${path.trimEnd('/')}/$name"
            val attributes = fileManager.attributesOfItemAtPath(childPath, error = null)
            val isDirectory = isDirectory(childPath)
            FileDataModel(
                name = name,
                isDirectory = isDirectory,
                path = childPath,
                size = when {
                    !isDirectory -> attributes.fileSize()
                    withFoldersSize -> getFolderSize(childPath)
                    else -> 0L
                },
                lastModified = (attributes?.get(NSFileModificationDate) as? NSDate)
                    ?.let { (it.timeIntervalSince1970 * 1000).toLong() }
                    ?: 0L,
            )
        }
    }

    // the enumerator does not follow symbolic links
    private fun getFolderSize(path: String): Long {
        val enumerator = fileManager.enumeratorAtPath(path) ?: return 0L
        var size = 0L
        while (enumerator.nextObject() != null) {
            val attributes = enumerator.fileAttributes
            if (attributes?.get(NSFileType) == NSFileTypeRegular) {
                size += attributes.fileSize()
            }
        }
        return size
    }

    // removeItemAtPath deletes a symbolic link, not its target
    override fun delete(path: String) {
        if (!fileManager.removeItemAtPath(path, error = null)) {
            FloconLogger.logError("Cannot delete '$path'", throwable = null)
        }
    }
}

private fun Map<Any?, *>?.fileSize(): Long = (this?.get(NSFileSize) as? NSNumber)?.longLongValue ?: 0L
