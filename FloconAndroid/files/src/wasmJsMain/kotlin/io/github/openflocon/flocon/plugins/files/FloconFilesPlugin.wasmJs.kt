package io.github.openflocon.flocon.plugins.files

import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconFile
import io.github.openflocon.flocon.plugins.files.model.fromdevice.FileDataModel
import io.github.openflocon.flocon.plugins.files.model.fromdevice.FileRootDataModel

internal actual fun fileDataSource(context: FloconContext): FileDataSource = FloconFilesDataSourceWasmJs()

// no file system: no root folder
private class FloconFilesDataSourceWasmJs : FileDataSource {
    override fun defaultRoots(): List<FileRootDataModel> = emptyList()
    override fun canonicalPath(path: String): String = path
    override fun isDirectory(path: String): Boolean = false
    override fun getFile(path: String): FloconFile? = null
    override fun getFolderContent(path: String, withFoldersSize: Boolean): List<FileDataModel> = emptyList()
    override fun delete(path: String) {}
}
