package io.github.openflocon.flocon.plugins.files

import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.plugins.files.model.fromdevice.FileRootDataModel
import java.io.File

// "files" and "caches" are the ids older desktop apps send without asking for the roots
internal actual fun fileDataSource(context: FloconContext): FileDataSource = JavaIoFileDataSource(
    defaultRoots = {
        listOf(
            FileRootDataModel(id = "files", name = "Working directory", path = systemFolder("user.dir")),
            FileRootDataModel(id = "caches", name = "Temp", path = systemFolder("java.io.tmpdir")),
            FileRootDataModel(id = "home", name = "Home", path = systemFolder("user.home")),
        )
    }
)

private fun systemFolder(property: String): String = File(System.getProperty(property)).absolutePath
