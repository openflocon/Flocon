package io.github.openflocon.flocon.plugins.files

import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconFile
import io.github.openflocon.flocon.plugins.files.model.fromdevice.FileDataModel

internal actual fun fileDataSource(context: FloconContext): FileDataSource {
    return FileDataSourceJvm()
}

internal class FileDataSourceJvm : FileDataSource {
    override fun getFile(
        path: String,
        isConstantPath: Boolean
    ): FloconFile? {
        return null
    }

    override fun getFolderContent(
        path: String,
        isConstantPath: Boolean
    ): List<FileDataModel> {
        return emptyList()
    }

    override fun deleteFile(path: String) {
        // no op
    }

    override fun deleteFolderContent(folder: FloconFile) {
        // no op
    }

}