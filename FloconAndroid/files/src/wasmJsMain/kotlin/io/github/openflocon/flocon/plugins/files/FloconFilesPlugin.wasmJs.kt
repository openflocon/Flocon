package io.github.openflocon.flocon.plugins.files

import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconFile
import io.github.openflocon.flocon.dsl.FloconMarker
import io.github.openflocon.flocon.plugins.files.model.fromdevice.FileDataModel

internal actual fun fileDataSource(context: FloconContext): FileDataSource = FloconFilesDataSourceWasmJs()

private class FloconFilesDataSourceWasmJs : FileDataSource {
    @FloconMarker
    override fun getFolderContent(path: String, isConstantPath: Boolean, withFoldersSize: Boolean): List<FileDataModel> = emptyList()

    @FloconMarker
    override fun getFile(path: String, isConstantPath: Boolean): FloconFile? = null

    override fun deleteFile(path: String) {}
    override fun deleteFiles(path: List<String>) {}

    @FloconMarker
    override fun deleteFolderContent(folder: FloconFile) {}
}
