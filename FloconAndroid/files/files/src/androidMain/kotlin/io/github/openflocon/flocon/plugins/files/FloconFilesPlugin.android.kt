package io.github.openflocon.flocon.plugins.files

import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.plugins.files.model.fromdevice.FileRootDataModel

internal actual fun fileDataSource(context: FloconContext): FileDataSource = JavaIoFileDataSource(
    defaultRoots = {
        listOf(
            FileRootDataModel(id = "files", name = "Files", path = context.context.filesDir.absolutePath),
            FileRootDataModel(id = "caches", name = "Caches", path = context.context.cacheDir.absolutePath),
            // getDatabasePath does not create anything: the folder is only shown once a database exists
            FileRootDataModel(id = "databases", name = "Databases", path = context.context.getDatabasePath("flocon").parentFile!!.absolutePath),
        )
    }
)
